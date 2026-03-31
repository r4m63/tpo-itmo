package lab1.task2.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация красно-черного дерева.
 * Красно-черное дерево - это самобалансирующееся двоичное дерево поиска,
 * в котором каждый узел имеет цвет (красный или черный) и соблюдаются
 * определенные свойства, которые обеспечивают сбалансированность дерева.
 */
public class RedBlackTree {

    /**
     * Узел красно-черного дерева.
     * Хранит значение, ссылки на детей и родителя, а также цвет узла.
     */
    public static class Node {
        int data;
        Node left, right, parent;
        boolean isRed;

        /**
         * Создает новый узел для вставки в дерево.
         * Новый узел по правилам красно-черного дерева сначала создается красным,
         * а дальнейшая балансировка и перекрашивание выполняются отдельно.
         *
         * @param data значение, которое будет храниться в узле
         */
        Node(int data) {
            this.data = data;
            this.isRed = true;
        }
    }

    private Node root;
    public Node TNULL; // Листовой узел

    /**
     * Создает пустое красно-черное дерево.
     * Внутри инициализируется специальный черный sentinel-узел {@code TNULL},
     * который используется вместо {@code null} для обозначения отсутствующих детей.
     * После этого корень дерева указывает на {@code TNULL}, то есть дерево
     * считается пустым.
     */
    public RedBlackTree() {
        TNULL = new Node(0);
        TNULL.isRed = false;
        root = TNULL;
    }

    /**
     * Ищет узел с указанным ключом, начиная с корня дерева.
     * Метод не реализует поиск сам, а делегирует работу рекурсивному helper-методу,
     * который спускается влево или вправо по правилам двоичного дерева поиска.
     *
     * @param key ключ, который нужно найти
     * @return найденный узел или {@code TNULL}, если такого ключа в дереве нет
     */
    public Node search(int key) {
        return searchTreeHelper(this.root, key);
    }

    /**
     * Возвращает текущий корень дерева.
     * Это полезно для внешних проверок, обходов или визуализации структуры.
     *
     * @return корневой узел или {@code TNULL}, если дерево пустое
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Выполняет симметричный обход дерева.
     * Сначала просматривается левое поддерево, затем текущий узел и после этого
     * правое,
     * поэтому узлы возвращаются в порядке возрастания ключей.
     *
     * @return список узлов в отсортированном по ключу порядке
     */
    public List<Node> inOrder() {
        var nodes = new ArrayList<Node>();
        inOrderHelper(nodes, this.root);
        return nodes;
    }

    /**
     * Рекурсивно обходит поддерево в симметричном порядке и добавляет узлы в
     * список.
     * Как только встречается {@code TNULL}, рекурсия для этой ветви завершается.
     *
     * @param nodes список, в который накапливаются найденные узлы
     * @param node  текущий узел, с которого продолжается обход
     */
    private void inOrderHelper(List<Node> nodes, Node node) {
        if (node != TNULL) {
            inOrderHelper(nodes, node.left);
            nodes.add(node);
            inOrderHelper(nodes, node.right);
        }
    }

    /**
     * Рекурсивно ищет ключ в поддереве.
     * Если ключ меньше значения текущего узла, поиск продолжается слева,
     * иначе справа. Поиск останавливается, когда найден точный ключ
     * или когда достигнут sentinel-узел {@code TNULL}.
     *
     * @param node корень текущего поддерева
     * @param key  искомый ключ
     * @return найденный узел или {@code TNULL}
     */
    private Node searchTreeHelper(Node node, int key) {
        if (node == TNULL || key == node.data) {
            return node;
        }

        if (key < node.data) {
            return searchTreeHelper(node.left, key);
        }
        return searchTreeHelper(node.right, key);
    }

    /**
     * Вставляет новый ключ в дерево.
     * Сначала метод находит место для нового узла как в обычном двоичном дереве
     * поиска,
     * затем связывает его с родителем и, если нужно, запускает балансировку.
     * Новый узел создается красным, а сохранение свойств красно-черного дерева
     * обеспечивается методом {@link #fixInsert(Node)}.
     *
     * @param key ключ, который нужно добавить в дерево
     */
    public void insert(int key) {
        Node node = new Node(key);
        node.parent = null;
        node.left = TNULL;
        node.right = TNULL;

        Node y = null;
        Node x = this.root;

        while (x != TNULL) {
            y = x;
            if (node.data < x.data) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        node.parent = y;
        if (y == null) {
            root = node; // Если дерево пустое
        } else if (node.data < y.data) {
            y.left = node;
        } else {
            y.right = node;
        }

        if (node.parent == null) {
            node.isRed = false; // Корень всегда черный
            return;
        }

        if (node.parent.parent == null) {
            return;
        }

        fixInsert(node);
    }

    /**
     * Восстанавливает свойства красно-черного дерева после вставки.
     * Метод рассматривает стандартные случаи балансировки: красный дядя,
     * внутренний изгиб и внешний изгиб. В зависимости от ситуации
     * выполняются перекрашивания и одно или два вращения.
     * Работа продолжается вверх по дереву, пока не исчезнет конфликт
     * вида "красный родитель - красный ребенок".
     *
     * @param k вставленный узел или текущий узел, относительно которого идет
     *          исправление
     */
    private void fixInsert(Node k) {
        Node u; // Дядя
        while (k.parent != null && k.parent.isRed) {
            if (k.parent == k.parent.parent.left) {
                u = k.parent.parent.right; // Дядя

                if (u != TNULL && u.isRed) { // Случай 1: дядя красный
                    u.isRed = false;
                    k.parent.isRed = false;
                    k.parent.parent.isRed = true;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.right) { // Случай 2: k - правый ребенок
                        k = k.parent;
                        leftRotate(k);
                    }
                    // Случай 3: k - левый ребенок
                    k.parent.isRed = false;
                    k.parent.parent.isRed = true;
                    rightRotate(k.parent.parent);
                }
            } else {
                u = k.parent.parent.left; // Дядя

                if (u != TNULL && u.isRed) { // Случай 1: дядя красный
                    u.isRed = false;
                    k.parent.isRed = false;
                    k.parent.parent.isRed = true;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) { // Случай 2: k - левый ребенок
                        k = k.parent;
                        rightRotate(k);
                    }
                    // Случай 3: k - правый ребенок
                    k.parent.isRed = false;
                    k.parent.parent.isRed = true;
                    leftRotate(k.parent.parent);
                }
            }
        }
        root.isRed = false; // Корень всегда черный
    }

    /**
     * Выполняет левый поворот вокруг узла {@code x}.
     * Правый ребенок узла {@code x} поднимается выше, а сам {@code x}
     * становится левым ребенком этого узла. Такой поворот не нарушает
     * порядок элементов, но меняет форму дерева для последующей балансировки.
     *
     * @param x узел, вокруг которого выполняется левый поворот
     */
    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != TNULL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    /**
     * Выполняет правый поворот вокруг узла {@code x}.
     * Левый ребенок узла {@code x} поднимается выше, а сам {@code x}
     * становится правым ребенком этого узла. Поворот используется
     * в операциях вставки и удаления для сохранения баланса дерева.
     *
     * @param x узел, вокруг которого выполняется правый поворот
     */
    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != TNULL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    /**
     * Печатает дерево в консоль в виде иерархической схемы.
     * Для каждого узла выводится его значение и цвет, а вспомогательный
     * метод рекурсивно формирует отступы, чтобы было видно структуру дерева.
     */
    public void printTree() {
        printTreeHelper(this.root, "", true);
    }

    /**
     * Рекурсивно печатает поддерево с накопленным отступом.
     * Флаг {@code last} показывает, является ли узел последним ребенком,
     * чтобы метод мог правильно подобрать графические префиксы ветвей.
     *
     * @param root   текущий узел поддерева
     * @param indent строка с уже накопленными отступами
     * @param last   признак того, что узел является правым или последним потомком
     */
    private void printTreeHelper(Node root, String indent, boolean last) {
        if (root != TNULL) {
            System.out.print(indent);
            if (last) {
                System.out.print("R----");
                indent += "   ";
            } else {
                System.out.print("L----");
                indent += "|  ";
            }
            System.out.println(root.data + (root.isRed ? "(R)" : "(B)"));
            printTreeHelper(root.left, indent, false);
            printTreeHelper(root.right, indent, true);
        }
    }

    /**
     * Удаляет узел с указанным ключом.
     * Сам алгоритм удаления вынесен в отдельный helper-метод,
     * который ищет узел, перестраивает связи и при необходимости
     * запускает восстановление свойств красно-черного дерева.
     *
     * @param data ключ узла, который нужно удалить
     */
    public void delete(int data) {
        deleteNodeHelper(this.root, data);
    }

    /**
     * Реализует удаление узла из красно-черного дерева.
     * Сначала метод ищет узел с нужным ключом, затем обрабатывает один из трех
     * случаев:
     * у узла нет левого ребенка, нет правого ребенка или есть оба ребенка.
     * В последнем случае узел заменяется своим следующим по порядку элементом
     * из правого поддерева. Если был удален черный узел, запускается балансировка
     * через {@link #fixDelete(Node)}.
     *
     * @param node корень поддерева, в котором начинается поиск
     * @param key  ключ удаляемого узла
     */
    private void deleteNodeHelper(Node node, int key) {
        Node z = TNULL;
        Node x, y;
        while (node != TNULL) {
            if (node.data == key) {
                z = node;
            }

            if (node.data <= key) {
                node = node.right;
            } else {
                node = node.left;
            }
        }

        if (z == TNULL) {
            throw new IllegalArgumentException("Node not found");
        }

        y = z;
        boolean yOriginalColor = y.isRed;
        if (z.left == TNULL) {
            x = z.right;
            rbTransplant(z, z.right);
        } else if (z.right == TNULL) {
            x = z.left;
            rbTransplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.isRed;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }

            rbTransplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.isRed = z.isRed;
        }
        if (!yOriginalColor) {
            fixDelete(x);
        }
    }

    /**
     * Заменяет одно поддерево другим, не меняя внутреннюю структуру самого
     * поддерева {@code v}.
     * Метод используется при удалении, когда нужно "пересадить" потомка
     * на место удаляемого узла и обновить ссылки на родителя.
     *
     * @param u узел, который заменяется
     * @param v узел или поддерево, которое ставится на место {@code u}
     */
    private void rbTransplant(Node u, Node v) {
        if (u.parent == null) {
            this.root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    /**
     * Находит узел с минимальным ключом в поддереве.
     * Для этого метод идет по левым ссылкам до тех пор,
     * пока не достигнет самого левого реального узла.
     *
     * @param node корень поддерева, в котором ищется минимум
     * @return узел с минимальным значением в этом поддереве
     */
    private Node minimum(Node node) {
        while (node.left != TNULL) {
            node = node.left;
        }
        return node;
    }

    /**
     * Восстанавливает свойства красно-черного дерева после удаления.
     * Метод исправляет ситуацию, когда после удаления черного узла
     * нарушается черная высота путей. Для этого рассматриваются
     * стандартные случаи в зависимости от цвета брата и его детей,
     * после чего выполняются перекрашивания и повороты.
     *
     * @param x узел, от которого начинается восстановление баланса
     */
    private void fixDelete(Node x) {
        while (x != root && !x.isRed) {
            if (x == x.parent.left) {
                Node w = x.parent.right;
                if (w.isRed) {
                    // Случай 1: дядя w красный
                    w.isRed = false;
                    x.parent.isRed = true;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (!w.left.isRed && !w.right.isRed) {
                    // Случай 2: оба ребенка дяди w черные
                    w.isRed = true;
                    x = x.parent;
                } else {
                    if (!w.right.isRed) {
                        // Случай 3: левый ребенок дяди w красный, правый — черный
                        w.left.isRed = false;
                        w.isRed = true;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    // Случай 4: правый ребенок дяди w красный
                    w.isRed = x.parent.isRed;
                    x.parent.isRed = false;
                    w.right.isRed = false;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                Node w = x.parent.left;
                if (w.isRed) {
                    // Случай 1: дядя w красный
                    w.isRed = false;
                    x.parent.isRed = true;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (!w.right.isRed && !w.left.isRed) {
                    // Случай 2: оба ребенка дяди w черные
                    w.isRed = true;
                    x = x.parent;
                } else {
                    if (!w.left.isRed) {
                        // Случай 3: правый ребенок дяди w красный, левый — черный
                        w.right.isRed = false;
                        w.isRed = true;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    // Случай 4: левый ребенок дяди w красный
                    w.isRed = x.parent.isRed;
                    x.parent.isRed = false;
                    w.left.isRed = false;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.isRed = false;
    }

    /**
     * Подсчитывает количество реальных узлов в дереве.
     * Sentinel-узел {@code TNULL} в подсчет не входит.
     *
     * @return общее количество элементов дерева
     */
    public int countNodes() {
        return countNodesHelper(this.root);
    }

    /**
     * Рекурсивно считает количество узлов в поддереве.
     * Для пустой ветви возвращается 0, иначе учитывается текущий узел
     * и результаты подсчета в левом и правом поддеревьях.
     *
     * @param node корень текущего поддерева
     * @return число узлов в этом поддереве
     */
    private int countNodesHelper(Node node) {
        if (node == TNULL) {
            return 0;
        }
        return 1 + countNodesHelper(node.left) + countNodesHelper(node.right);
    }

    /**
     * Проверяет, удовлетворяет ли дерево основным свойствам красно-черного дерева.
     * Последовательно контролируется цвет корня, корректность структуры BST,
     * отсутствие подряд идущих красных узлов и одинаковая черная высота
     * на всех путях от корня до листьев.
     *
     * @return {@code true}, если дерево корректно, иначе {@code false}
     */
    public boolean isValidRedBlackTree() {
        // Корень должен быть черным
        if (root.isRed) {
            return false;
        }
        // Проверка свойств BST
        if (!isValidBST(root)) {
            return false;
        }
        // Проверка цвета узлов
        if (!isProperlyColored(root)) {
            return false;
        }
        // Проверка черной высоты
        if (!hasEqualBlackHeight(root)) {
            return false;
        }
        return true;
    }

    /**
     * Проверяет, что дерево сохраняет свойство двоичного дерева поиска.
     * Для этого используется helper-метод с допустимым диапазоном значений
     * для каждого поддерева.
     *
     * @param node корень проверяемого поддерева
     * @return {@code true}, если порядок ключей корректен
     */
    private boolean isValidBST(Node node) {
        return isValidBSTHelper(node, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Рекурсивно проверяет, что значение каждого узла попадает в допустимый
     * диапазон.
     * Для левого поддерева верхняя граница сужается значением текущего узла,
     * а для правого поддерева это значение становится нижней границей.
     *
     * @param node текущий узел
     * @param min  минимально допустимое значение
     * @param max  максимально допустимое значение
     * @return {@code true}, если поддерево удовлетворяет свойству BST
     */
    private boolean isValidBSTHelper(Node node, int min, int max) {
        if (node == TNULL) {
            return true;
        }
        if (node.data < min || node.data > max) {
            return false;
        }
        return isValidBSTHelper(node.left, min, node.data) && isValidBSTHelper(node.right, node.data, max);
    }

    /**
     * Проверяет правило окраски красно-черного дерева:
     * у красного узла не может быть красных детей.
     * Метод рекурсивно проходит по всем узлам и сразу завершает проверку,
     * если находит нарушение.
     *
     * @param node корень текущего поддерева
     * @return {@code true}, если окраска корректна
     */
    private boolean isProperlyColored(Node node) {
        if (node == TNULL) {
            return true;
        }
        // Если узел красный, оба его ребенка должны быть черными
        if (node.isRed) {
            if (node.left.isRed || node.right.isRed) {
                return false;
            }
        }
        // Рекурсивно проверяем левое и правое поддеревья
        return isProperlyColored(node.left) && isProperlyColored(node.right);
    }

    /**
     * Проверяет, что во всех путях от текущего узла до листьев
     * содержится одинаковое количество черных узлов.
     * Для этого сравнивается черная высота левого и правого поддерева,
     * а затем аналогичная проверка рекурсивно выполняется ниже.
     *
     * @param node корень текущего поддерева
     * @return {@code true}, если черная высота одинакова на всех путях
     */
    private boolean hasEqualBlackHeight(Node node) {
        if (node == TNULL) {
            return true;
        }
        // Черная высота левого и правого поддеревьев должна быть одинаковой
        int leftBlackHeight = countBlackHeight(node.left);
        int rightBlackHeight = countBlackHeight(node.right);
        if (leftBlackHeight != rightBlackHeight) {
            return false;
        }
        // Рекурсивно проверяем левое и правое поддеревья
        return hasEqualBlackHeight(node.left) && hasEqualBlackHeight(node.right);
    }

    /**
     * Считает черную высоту вдоль левой ветви поддерева.
     * Черная высота показывает, сколько черных узлов встречается
     * на пути до листового sentinel-узла {@code TNULL}, который тоже считается
     * черным.
     * Метод используется как часть проверки корректности дерева.
     *
     * @param node корень поддерева
     * @return черная высота выбранного пути
     */
    private int countBlackHeight(Node node) {
        if (node == TNULL) {
            return 1; // Листовой узел (TNULL) считается черным
        }
        int count = node.isRed ? 0 : 1; // Черный узел добавляет 1 к высоте
        count += countBlackHeight(node.left); // Рекурсивно считаем высоту
        return count;
    }
}
