// Knapsack implementation using branch and bound

import java.util.*;
import java.io.*;

 class knapsack {

    private static int size;
    private static int capacity;

    // Main method where we read the file, knapsack items and solve
     public static void main(String args[]) throws FileNotFoundException
    {


        Scanner scan = new Scanner(System.in);
        System.out.println("Enter filename: ");
        String fileName = scan.next();

        Scanner input = new Scanner (new File(fileName));
        String line = input.nextLine();
        String[] details = line.split(" ");
        size = Integer.parseInt(details[0]);
        capacity = Integer.parseInt(details[1]);
        Item arr[] = new Item[size];

        for(int i=0; i<size; i++){
            line = input.nextLine();
            details = line.split(" ");
            arr[i] = new Item(Integer.parseInt(details[0]),Integer.parseInt(details[1]),i);
        }

        try{
            solve(arr);
        }
        catch(IOException e) {

        }





    }

    // Function to calculate upper bound
    static int calculateUpperBound(int itemValue, int itemWeight,
                            int idx, Item arr[])
    {
        int value = itemValue;
        int weight = itemWeight;
        for (int i = idx; i < size; i++) {
            if (weight + arr[i].weight
                    <= capacity) {
                weight += arr[i].weight;
                value -= arr[i].value;
            }
            else {
                value -= (int)(capacity
                        - weight)
                        / arr[i].weight
                        * arr[i].value;
                break;
            }
        }
        return value;
    }

    // Calculate lower bound
    static int calculateLowerBound(int itemValue, int itemWeight,
                            int idx, Item arr[])
    {
        int value = itemValue;
        int weight = itemWeight;
        for (int i = idx; i < size; i++) {
            if (weight + arr[i].weight
                    <= capacity) {
                weight += arr[i].weight;
                value -= arr[i].value;
            }
            else {
                break;
            }
        }
        return value;
    }

    static void assign(Node a, int upperBound, int lowerBound,
                       int level, boolean flag,
                       int itemValue, int itemWeight)
    {
        a.upperBound = upperBound;
        a.lowerBound = lowerBound;
        a.level = level;
        a.flag = flag;
        a.itemValue = itemValue;
        a.itemWeight = itemWeight;
    }

     static void solve(Item arr[]) throws IOException
    {
        FileWriter fileWriter = new FileWriter("output.txt");
        PrintWriter printItem = new PrintWriter(fileWriter);
        // Sort the items based on the
        // profit/weight ratio
        Arrays.sort(arr, new sortByRatio());

        Node current, left, right;
        current = new Node();
        left = new Node();
        right = new Node();


        int minlowerBound = 0, finallowerBound
                = Integer.MAX_VALUE;
        current.itemValue = current.itemWeight = current.upperBound
                = current.lowerBound = 0;
        current.level = 0;
        current.flag = false;

        // Priority queue to store elements

        PriorityQueue<Node> pq
                = new PriorityQueue<Node>(
                new sortByC());


        pq.add(current);


        boolean currPath[] = new boolean[size];
        boolean finalPath[] = new boolean[size];

        while (!pq.isEmpty()) {
            current = pq.poll();
            if (current.upperBound > minlowerBound
                    || current.upperBound >= finallowerBound) {
                continue;
            }

            if (current.level != 0)
                currPath[current.level - 1]
                        = current.flag;

            if (current.level == size) {
                if (current.lowerBound < finallowerBound) {
                    // Reached last level
                    for (int i = 0; i < size; i++)
                        finalPath[arr[i].idx]
                                = currPath[i];
                    finallowerBound = current.lowerBound;
                }
                continue;
            }

            int level = current.level;


            assign(right, calculateUpperBound(current.itemValue,
                            current.itemWeight,
                            level + 1, arr),
                    calculateLowerBound(current.itemValue, current.itemWeight,
                            level + 1, arr),
                    level + 1, false,
                    current.itemValue, current.itemWeight);

            if (current.itemWeight + arr[current.level].weight
                    <= capacity) {


                left.upperBound = calculateUpperBound(
                        current.itemValue
                                - arr[level].value,
                        current.itemWeight
                                + arr[level].weight,
                        level + 1, arr);
                left.lowerBound = calculateLowerBound(
                        current.itemValue
                                - arr[level].value,
                        current.itemWeight
                                + arr[level].weight,
                        level + 1,
                        arr);
                assign(left, left.upperBound, left.lowerBound,
                        level + 1, true,
                        current.itemValue - arr[level].value,
                        current.itemWeight
                                + arr[level].weight);
            }


            else {


                left.upperBound = left.lowerBound = 1;
            }

            // Update minlowerBound
            minlowerBound = Math.min(minlowerBound, left.lowerBound);
            minlowerBound = Math.min(minlowerBound, right.lowerBound);

            if (minlowerBound >= left.upperBound)
                pq.add(new Node(left));
            if (minlowerBound >= right.upperBound)
                pq.add(new Node(right));
        }
        printItem.println(-finallowerBound);
        for (int i = 0; i < size; i++) {
            if (finalPath[i])
                printItem.print("1 ");
            else
                printItem.print("0 ");
        }

        printItem.close();

    }

}


class Item {


    int weight;


    int value;


    int idx;
    Item() {}
    Item(int value, int weight,
                int idx)
    {
        this.value = value;
        this.weight = weight;
        this.idx = idx;
    }
}
class Node {

    int upperBound;


    int lowerBound;


    int level;


    boolean flag;


    int itemValue;


    int itemWeight;
    Node() {}
    Node(Node cpy)
    {
        this.itemValue = cpy.itemValue;
        this.itemWeight = cpy.itemWeight;
        this.upperBound = cpy.upperBound;
        this.lowerBound = cpy.lowerBound;
        this.level = cpy.level;
        this.flag = cpy.flag;
    }
}
class sortByC implements Comparator<Node> {
      public int compare(Node a, Node b)
    {
        boolean temp = a.lowerBound > b.lowerBound;
        return temp ? 1 : -1;
    }
}

class sortByRatio implements Comparator<Item> {
     public int compare(Item a, Item b)
    {
        boolean temp = (int)a.value
                / a.weight
                > (int)b.value
                / b.weight;
        return temp ? -1 : 1;
    }
}






