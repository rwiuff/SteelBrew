package org.rwiuff.steelbrew.forge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.rwiuff.steelbrew.brewer.Brewer;
import org.rwiuff.steelbrew.forge.Barista.Order;

public class Forge {

    private static volatile Forge instance = null;
    private static boolean wsl = false; // Bool that enables wsl command call
    private static ArrayList<Brewer> brewers = new ArrayList<>(); // All the brewers
    private static ArrayList<ArrayList<String>> orders = new ArrayList<>(); // Map with all the commands to be executed
                                                                            // during simulation

    private Forge() {
        System.out.println(
                "+---------------------------------------------------------------------------------------------------------------+");
        System.out.println(
                "|                                                Forge warmed up                                                |");
        System.out.println(
                "+---------------------------------------------------------------------------------------------------------------+");
    }

    public static Forge getInstance() { // Instantiates Forge as a Singleton
        if (instance == null) {
            synchronized (Forge.class) {
                if (instance == null) {
                    instance = new Forge();
                }
            }
        }
        return instance;
    }

    public static void enableWSL(boolean wsl) {
        Forge.wsl = wsl;
    }

    private static ArrayList<String> wsl() { // Adds wsl command calls
        ArrayList<String> list = new ArrayList<>();
        list.add("wsl");
        list.add("-d");
        list.add("Ubuntu");
        return list;
    }

    public static void addBrewer(Brewer brewer) {
        Forge.brewers.add(brewer);
    }

    public static void simulate() { // Finds all the test names and creates a list of commands to execute
        prelude();
        ArrayList<String> tests = new ArrayList<>();
        brewers.forEach(b -> b.getTestbenches().forEach(t -> tests.add(t.getName())));
        for (String test : tests) {
            ArrayList<String> list = new ArrayList<>();
            if (wsl)
                list = wsl();
            list.add("make");
            list.add(test);
            orders.add(list);
        }
        new Barista(orders);
    }

    public static ArrayList<Brewer> getBrewers() {
        return brewers;
    }

    public static void prelude() { // Calls for brewers to create testbench files
        brewers.forEach(b -> b.grind());
        new Makefile(brewers); // Creates makefile
    }

    public static void returnResults(ArrayList<Order> results) { // Prints output from simulations
        System.out.println("\nTests are done! Showing results\n");
        for (Order result : results) {
            System.out.println(
                    "+---------------------------------------------------------------------------------------------------------------+");
            String[] command = result.command.split("\\s");
            System.out.println(String.format("| Test: %s %s |", command[command.length - 1],
                    " ".repeat(102 - command[command.length - 1].length())));
            System.out.println(
                    "+---------------------------------------------------------------------------------------------------------------+");
            List<String> filtered = Arrays.asList(result.stdout.split("\\R")).stream()
                    .filter(s -> s.contains("Peek on") || s.contains("Poke on") || s.contains("Expected "))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty())
                System.out.print("+");
            System.out.println(String.join(System.lineSeparator() + "+", filtered));
            System.out.println(
                    "+---------------------------------------------------------------------------------------------------------------+");
            if (!result.stderr.isBlank()) {
                System.out.println("Reported errors:");
                System.out.println(result.stderr);
            }
        }
    }
}

class Barista {

    static class Order {
        String command;
        String stdout;
        String stderr;
        int exitCode;

        public Order(String command, String stdout, String stderr, int exitCode) {
            this.command = command;
            this.stdout = stdout;
            this.stderr = stderr;
            this.exitCode = exitCode;
        }

        public String toString() {
            return "Command: " + command + "\nExit Code: " + exitCode + "\n---STDOUT---\n" + stdout + "\n---STDERR---\n"
                    + stderr + "\n";
        }
    }

    public Barista(ArrayList<ArrayList<String>> orders) {
        ExecutorService executor = Executors.newCachedThreadPool(); // Threadpool that reuses threads if needed
        ArrayList<Order> results = new ArrayList<>();
        List<Future<Order>> futures = orders.stream()
                .map(commandArgs -> executor.submit(() -> runProcess(commandArgs, executor))).toList();

        for (Future<Order> future : futures) {
            Order result;
            try {
                result = future.get();
                results.add(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        Forge.returnResults(results);
        executor.shutdown();
    }

    private static Order runProcess(ArrayList<String> commandArgs, ExecutorService executor) {
        ProcessBuilder builder = new ProcessBuilder(commandArgs);
        Process process;
        try {
            process = builder.start();
            StringBuilder stdout = new StringBuilder();
            StringBuilder stderr = new StringBuilder();
            String commandString = String.join(" ", commandArgs);

            Future<?> stdoutFuture = executor.submit(() -> readStream(process.getInputStream(), stdout));
            Future<?> stderrFuture = executor.submit(() -> readStream(process.getErrorStream(), stdout));
            int exitCode = process.waitFor();
            stdoutFuture.get();
            stderrFuture.get();
            return new Order(commandString, stdout.toString(), stderr.toString(), exitCode);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void readStream(InputStream inputStream, StringBuilder output) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
