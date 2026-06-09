"""Построение PNG-графиков из JMeter .jtl для отчёта lab4.

Запуск:
    /Users/ramil/.claude/skills/gost-report/.venv/bin/python lab4/.claude/gost-report/charts.py
"""
import csv
from collections import defaultdict
from pathlib import Path
from statistics import mean

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

LAB4 = Path(__file__).resolve().parents[2]
RESULTS = LAB4 / 'jmeter' / 'results'
FIGURES = LAB4 / 'docs' / 'figures'
FIGURES.mkdir(parents=True, exist_ok=True)

SLO_MS = 720
CONFIGS = [
    (1, 4500),
    (2, 7300),
    (3, 9300),
]


def percentile(sorted_xs, p):
    if not sorted_xs:
        return 0
    k = max(0, min(len(sorted_xs) - 1, int(round(p * (len(sorted_xs) - 1)))))
    return sorted_xs[k]


def rows(path):
    with open(path, newline='') as f:
        for r in csv.DictReader(f):
            yield r


def load_summary(path):
    elapsed = []
    timestamps = []
    fails = 0
    for r in rows(path):
        elapsed.append(int(r['elapsed']))
        timestamps.append(int(r['timeStamp']))
        if r['success'] == 'false':
            fails += 1
    elapsed_sorted = sorted(elapsed)
    n = len(elapsed)
    duration_s = (max(timestamps) - min(timestamps)) / 1000.0 if timestamps else 1.0
    throughput = n / duration_s if duration_s > 0 else 0
    return {
        'n': n,
        'avg': mean(elapsed) if elapsed else 0,
        'p90': percentile(elapsed_sorted, 0.90),
        'p95': percentile(elapsed_sorted, 0.95),
        'p99': percentile(elapsed_sorted, 0.99),
        'fails': fails,
        'throughput': throughput,
    }


def stress_steady_steps(path, min_count=30):
    per_users = defaultdict(list)
    for r in rows(path):
        users = int(r['allThreads'])
        per_users[users].append(int(r['elapsed']))
    steps = []
    for users in sorted(per_users):
        e = sorted(per_users[users])
        if len(e) < min_count:
            continue
        steps.append({
            'users': users,
            'n': len(e),
            'avg': mean(e),
            'p95': percentile(e, 0.95),
        })
    return steps


def chart_throughput():
    summaries = [load_summary(RESULTS / 'load' / f'config{c}.jtl') for c, _ in CONFIGS]
    labels = [f'Config {c}\n(${p})' for c, p in CONFIGS]
    values = [s['throughput'] for s in summaries]

    fig, ax = plt.subplots(figsize=(7, 4.5))
    bars = ax.bar(labels, values, color=['#cc4444', '#dd9944', '#3a7d44'])
    target = 9 * 20 / 60.0
    ax.axhline(target, color='#1f77b4', linestyle='--', linewidth=1.2,
               label=f'Целевая нагрузка {target:.1f} req/s (180 req/min)')
    ax.set_ylabel('Пропускная способность, req/s')
    ax.set_ylim(0, max(values + [target]) * 1.25)
    ax.grid(axis='y', linestyle=':', alpha=0.4)
    ax.legend(loc='upper right')
    for b, v in zip(bars, values):
        ax.text(b.get_x() + b.get_width() / 2, v + 0.05,
                f'{v:.2f} req/s', ha='center', fontsize=10)
    ax.set_title('Пропускная способность под нагрузкой 9 пользователей x 20 req/min')
    fig.tight_layout()
    out = FIGURES / 'load_throughput.png'
    fig.savefig(out, dpi=160)
    plt.close(fig)
    print(f'wrote {out}')
    return summaries


def chart_response_time(summaries):
    labels = [f'Config {c}\n(${p})' for c, p in CONFIGS]
    avg_vals = [s['avg'] for s in summaries]
    p95_vals = [s['p95'] for s in summaries]

    fig, ax = plt.subplots(figsize=(7, 4.5))
    x = range(len(labels))
    width = 0.35
    bars_avg = ax.bar([i - width / 2 for i in x], avg_vals, width,
                       label='Average', color='#4f81bd')
    bars_p95 = ax.bar([i + width / 2 for i in x], p95_vals, width,
                       label='95-й перцентиль', color='#c0504d')
    ax.axhline(SLO_MS, color='#1f77b4', linestyle='--', linewidth=1.4,
               label=f'SLO {SLO_MS} ms')
    ax.set_xticks(list(x))
    ax.set_xticklabels(labels)
    ax.set_ylabel('Время отклика, мс')
    ax.set_ylim(0, max(p95_vals + [SLO_MS]) * 1.25)
    ax.grid(axis='y', linestyle=':', alpha=0.4)
    ax.legend(loc='upper right')
    for bars, vals in [(bars_avg, avg_vals), (bars_p95, p95_vals)]:
        for b, v in zip(bars, vals):
            ax.text(b.get_x() + b.get_width() / 2, v + 15,
                    f'{int(v)}', ha='center', fontsize=9)
    ax.set_title('Время отклика по конфигурациям')
    fig.tight_layout()
    out = FIGURES / 'load_response_time.png'
    fig.savefig(out, dpi=160)
    plt.close(fig)
    print(f'wrote {out}')


def chart_stress():
    path = RESULTS / 'stress' / 'stress-config3.jtl'
    steps = stress_steady_steps(path)
    users = [s['users'] for s in steps]
    avgs = [s['avg'] for s in steps]
    p95s = [s['p95'] for s in steps]

    breaking = None
    for s in steps:
        if s['avg'] > SLO_MS or s['p95'] > SLO_MS:
            breaking = s['users']
            break

    fig, ax = plt.subplots(figsize=(8, 5))
    ax.plot(users, avgs, marker='o', linewidth=2, color='#4f81bd', label='Average')
    ax.plot(users, p95s, marker='s', linewidth=2, color='#c0504d', label='95-й перцентиль')
    ax.axhline(SLO_MS, color='#1f77b4', linestyle='--', linewidth=1.4,
               label=f'SLO {SLO_MS} ms')
    if breaking is not None:
        ax.axvline(breaking, color='#999999', linestyle=':', linewidth=1.4,
                   label=f'Точка перелома: {breaking} польз.')
    ax.set_xlabel('Количество параллельных пользователей')
    ax.set_ylabel('Время отклика, мс')
    ax.set_title('Зависимость времени отклика от нагрузки (config 3, $9300)')
    ax.set_xticks(users)
    ax.grid(linestyle=':', alpha=0.4)
    ax.legend(loc='upper left')
    for u, v in zip(users, avgs):
        ax.text(u, v - 35, f'{int(v)}', ha='center', fontsize=8, color='#4f81bd')
    for u, v in zip(users, p95s):
        ax.text(u, v + 18, f'{int(v)}', ha='center', fontsize=8, color='#c0504d')
    fig.tight_layout()
    out = FIGURES / 'stress_response_vs_load.png'
    fig.savefig(out, dpi=160)
    plt.close(fig)
    print(f'wrote {out}')
    return steps, breaking


if __name__ == '__main__':
    summaries = chart_throughput()
    chart_response_time(summaries)
    chart_stress()
