#!/usr/bin/env python3
"""Print load/stress summary from a JMeter .jtl (CSV) file.

Usage:
  summarize.py load    lab4/jmeter/results/load/config1.jtl
  summarize.py stress  lab4/jmeter/results/stress/stress-config2.jtl
  summarize.py compare 4500:.../config1.jtl 7300:.../config2.jtl 9300:.../config3.jtl
"""
import csv
import sys
from collections import defaultdict
from statistics import mean

SLO_MS = 720


def percentile(sorted_xs, p):
    if not sorted_xs:
        return 0
    k = max(0, min(len(sorted_xs) - 1, int(round(p * (len(sorted_xs) - 1)))))
    return sorted_xs[k]


def load_rows(path):
    with open(path, newline='') as f:
        for row in csv.DictReader(f):
            yield row


def load_summary(path):
    elapsed, codes, fails = [], defaultdict(int), 0
    for r in load_rows(path):
        elapsed.append(int(r['elapsed']))
        codes[r['responseCode']] += 1
        if r['success'] == 'false':
            fails += 1
    elapsed.sort()
    n = len(elapsed)
    if n == 0:
        print('empty .jtl')
        return
    avg = mean(elapsed)
    p90 = percentile(elapsed, 0.90)
    p95 = percentile(elapsed, 0.95)
    p99 = percentile(elapsed, 0.99)
    over = sum(1 for e in elapsed if e > SLO_MS)
    verdict = 'PASS' if (fails == 0 and p95 <= SLO_MS) else 'FAIL'
    print(f'samples = {n}')
    print(f'avg     = {avg:7.1f} ms')
    print(f'p90     = {p90:7d} ms')
    print(f'p95     = {p95:7d} ms   (SLO {SLO_MS} ms)')
    print(f'p99     = {p99:7d} ms')
    print(f'over SLO = {over} ({over / n * 100:.1f}%)')
    print(f'fails    = {fails} ({fails / n * 100:.1f}%)')
    print(f'codes    = {dict(codes)}')
    print(f'verdict  = {verdict}')


def stress_summary(path):
    per_thread = defaultdict(list)
    per_thread_codes = defaultdict(lambda: defaultdict(int))
    total = 0
    for r in load_rows(path):
        users = int(r['allThreads'])
        per_thread[users].append(int(r['elapsed']))
        per_thread_codes[users][r['responseCode']] += 1
        total += 1
    # Между ступенями `allThreads` плавно меняется (ramp-up следующей группы),
    # из-за чего в .jtl появляются «промежуточные» значения с малым числом
    # сэмплов и шумным p95. Steady-state ступень даёт минимум ~50 сэмплов
    # (низкое число пользователей × короткая ступень), переходы — единицы.
    min_count = 30
    steady = {u: e for u, e in per_thread.items() if len(e) >= min_count}
    transient_total = sum(len(e) for u, e in per_thread.items() if u not in steady)
    print(f'{"users":>5} | {"count":>5} | {"avg":>6} | {"p95":>6} | over_SLO | codes')
    print('-' * 70)
    breaking = None
    for users in sorted(steady):
        e = sorted(steady[users])
        n = len(e)
        avg = mean(e)
        p95 = percentile(e, 0.95)
        over = sum(1 for x in e if x > SLO_MS)
        codes = dict(per_thread_codes[users])
        marker = ' '
        if avg > SLO_MS or p95 > SLO_MS:
            marker = '*'
            if breaking is None:
                breaking = users
        print(f'{users:>5} | {n:>5} | {avg:>6.0f} | {p95:>6d} | {over:>8} | {codes} {marker}')
    if transient_total:
        print(f'(hid {transient_total} samples from ramp-up transitions; '
              f'kept rows with count >= {min_count})')
    if not steady:
        print('\nno steady-state steps detected — check the run')
        return
    if breaking is not None:
        print(f'\nbreaking point: {breaking} users (first step where avg or p95 > {SLO_MS} ms)')
    else:
        print(f'\nno breaking point in this run (system stayed under {SLO_MS} ms)')


def compare(args):
    rows = []
    for arg in args:
        price_str, path = arg.split(':', 1)
        price = int(price_str)
        elapsed, codes, fails = [], defaultdict(int), 0
        for r in load_rows(path):
            elapsed.append(int(r['elapsed']))
            codes[r['responseCode']] += 1
            if r['success'] == 'false':
                fails += 1
        elapsed.sort()
        n = len(elapsed)
        avg = mean(elapsed) if n else 0
        p95 = percentile(elapsed, 0.95) if n else 0
        verdict = 'PASS' if (n > 0 and fails == 0 and p95 <= SLO_MS) else 'FAIL'
        rows.append({
            'price': price, 'n': n, 'avg': avg, 'p95': p95,
            'fails': fails, 'codes': dict(codes), 'verdict': verdict,
        })

    print(f'{"price":>7} | {"n":>6} | {"avg":>6} | {"p95":>6} | {"fails":>5} | verdict')
    print('-' * 60)
    for r in rows:
        print(f'${r["price"]:>6} | {r["n"]:>6} | {r["avg"]:>6.0f} | '
              f'{r["p95"]:>6d} | {r["fails"]:>5} | {r["verdict"]}')
    passed = [r for r in rows if r['verdict'] == 'PASS']
    if passed:
        cheapest = min(passed, key=lambda r: r['price'])
        print(f'\nRecommended: ${cheapest["price"]} '
              f'(cheapest config meeting SLO {SLO_MS} ms)')
    else:
        print(f'\nNo config met SLO {SLO_MS} ms.')


def main():
    if len(sys.argv) < 2:
        print(__doc__)
        sys.exit(2)
    mode = sys.argv[1]
    if mode in ('load', 'stress'):
        if len(sys.argv) != 3:
            print(__doc__)
            sys.exit(2)
        path = sys.argv[2]
        (load_summary if mode == 'load' else stress_summary)(path)
    elif mode == 'compare':
        if len(sys.argv) < 3:
            print(__doc__)
            sys.exit(2)
        compare(sys.argv[2:])
    else:
        print(__doc__)
        sys.exit(2)


if __name__ == '__main__':
    main()
