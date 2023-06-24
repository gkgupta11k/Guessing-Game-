import java.util.*;
import java.util.stream.*;

class Individual {
    int value;
    int fitness;

    public Individual(int value, int fitness) {
        this.value = value;
        this.fitness = fitness;
    }
}

public class GuessingNumberGame {
    static Random rand = new Random();
    static int targetNumber;
    static int POPULATION_SIZE = 100;
    static double MUTATION_RATE = 0.01;
    static double CROSSOVER_RATE = 0.75;
    static double ELITISM_RATE = 0.1;

    static int fitness(int number) {
        return Math.abs(number - targetNumber);
    }

    static int generateNumber() {
        return rand.nextInt(101);
    }

    static Individual crossover(Individual ind1, Individual ind2) {
        String binaryInd1 = Integer.toBinaryString(ind1.value);
        String binaryInd2 = Integer.toBinaryString(ind2.value);

        // Ensure the binary strings have equal length by padding with zeros
        while (binaryInd1.length() < binaryInd2.length())
            binaryInd1 = "0" + binaryInd1;
        while (binaryInd2.length() < binaryInd1.length())
            binaryInd2 = "0" + binaryInd2;

        int crossoverPoint = rand.nextInt(binaryInd1.length());
        String childBinary = binaryInd1.substring(0, crossoverPoint) + binaryInd2.substring(crossoverPoint);
        int childValue = Integer.parseInt(childBinary, 2);
        return new Individual(childValue, fitness(childValue));
    }

    static Individual mutate(Individual ind) {
        String binaryInd = Integer.toBinaryString(ind.value);
        int mutationPoint = rand.nextInt(binaryInd.length());
        binaryInd = binaryInd.substring(0, mutationPoint) + (binaryInd.charAt(mutationPoint) == '0' ? '1' : '0')
                + binaryInd.substring(mutationPoint + 1);
        int mutatedValue = Integer.parseInt(binaryInd, 2);
        return new Individual(mutatedValue, fitness(mutatedValue));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Think of a number between 1 and 100 (inclusive).");
        System.out.println("The computer will try to guess it. Press ENTER when you're ready.");
        scanner.nextLine();

        System.out
                .println("Please enter the number for the computer to guess (this will be hidden from the algorithm):");
        targetNumber = scanner.nextInt();
        scanner.nextLine(); // consume newline

        List<Individual> population = IntStream.range(0, POPULATION_SIZE)
                .mapToObj(i -> new Individual(generateNumber(), 0))
                .collect(Collectors.toList());

        boolean found = false;
        for (int generation = 0; generation < 100; generation++) {
            System.out.printf("Generation #%d\n", generation);

            // Calculate fitness for each individual in the population
            population.forEach(individual -> individual.fitness = fitness(individual.value));

            // Sort the population by their fitness (ascending)
            population.sort(Comparator.comparingInt(ind -> ind.fitness));

            List<Individual> newPopulation = new ArrayList<>();

            // Elitism: carry the fittest individuals over to the new population
            int elites = (int) (POPULATION_SIZE * ELITISM_RATE);
            newPopulation.addAll(population.subList(0, elites));

            // Create new individuals through crossover, then apply mutation
            for (int i = elites; i < POPULATION_SIZE; i++) {
                Individual parent1 = population.get(rand.nextInt(POPULATION_SIZE));
                Individual parent2 = population.get(rand.nextInt(POPULATION_SIZE));
                Individual child = rand.nextDouble() < CROSSOVER_RATE ? crossover(parent1, parent2) : parent1;
                child = rand.nextDouble() < MUTATION_RATE ? mutate(child) : child;

                newPopulation.add(child);
            }

            population = newPopulation;

            // Make a guess
            int guess = population.get(0).value;
            System.out.println("The computer guesses: " + guess);

            // Check the guess
            if (guess == targetNumber) {
                System.out.println("The computer has successfully guessed your number!");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println(
                    "The computer failed to guess your number within 100 generations. Try again or increase the number of generations.");
        }
    }
}
