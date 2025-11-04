package com.example;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

public class Grafo {
    private Map<String, List<String>> adjacencia;

    public Grafo() {
        adjacencia = new HashMap<>();
    }

    public void adicionarVertice(String vertice) {
        adjacencia.putIfAbsent(vertice, new ArrayList<>());
    }
    
    public void adicionarAresta(String vertice1, String vertice2) {
        adjacencia.get(vertice1).add(vertice2);
        adjacencia.get(vertice2).add(vertice1);
    }

    public void salvarGrafo(String caminhoArquivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (Map.Entry<String, List<String>> entry : adjacencia.entrySet()) {
                String vertice = entry.getKey();
                List<String> arestas = entry.getValue();
                writer.write(vertice + "->" + String.join(", ", arestas));
                writer.newLine();
            }
        }
    }

    public void carregarGrafo(String caminhoArquivo) {
        try {
            File xmlFile = new File(caminhoArquivo);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList edgeList = doc.getElementsByTagName("edge");

            for (int i = 0; i < edgeList.getLength(); i++) {
                Node edgeNode = edgeList.item(i);
                if (edgeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element edgeElement = (Element) edgeNode;
                    String source = edgeElement.getAttribute("source");
                    String target = edgeElement.getAttribute("target");

                    adicionarVertice(source);
                    adicionarVertice(target);
                    adicionarAresta(source, target);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imprimirGrafo() {
        for (Map.Entry<String, List<String>> entry : adjacencia.entrySet()) {
            System.out.println(entry.getKey() + " -> " + String.join(", ", entry.getValue()));
        }
    }
    /*
     * Betweenness Centrality mede a importância de um vértice com base na quantidade
     * de vezes que ele aparece nos caminhos mais curtos entre outros pares de vértices.
     */
    public Map<String, Double> calcularBetweennessCentrality() {
        Map<String, Double> betweenness = new HashMap<>();
        for (String v : adjacencia.keySet()) {
            betweenness.put(v, 0.0);
        }

        for (String s : adjacencia.keySet()) {
            Stack<String> stack = new Stack<>();
            Map<String, List<String>> predecessors = new HashMap<>();
            Map<String, Integer> sigma = new HashMap<>();
            Map<String, Integer> distancia = new HashMap<>();
            Queue<String> queue = new LinkedList<>();

            for (String v : adjacencia.keySet()) {
                predecessors.put(v, new ArrayList<>());
                sigma.put(v, 0);
                distancia.put(v, -1);
            }
            sigma.put(s, 1);
            distancia.put(s, 0);
            queue.add(s);

            while (!queue.isEmpty()) {
                String v = queue.poll();
                stack.push(v);

                for (String w : adjacencia.get(v)) {
                    if (distancia.get(w) < 0) {
                        distancia.put(w, distancia.get(v) + 1);
                        queue.add(w);
                    }
                    if (distancia.get(w) == distancia.get(v) + 1) {
                        sigma.put(w, sigma.get(w) + sigma.get(v));
                        predecessors.get(w).add(v);
                    }
                }
            }

            Map<String, Double> delta = new HashMap<>();
            for (String v : adjacencia.keySet()) {
                delta.put(v, 0.0);
            }

            while (!stack.isEmpty()) {
                String w = stack.pop();
                for (String v : predecessors.get(w)) {
                    double c = ((double) sigma.get(v) / sigma.get(w)) * (1 + delta.get(w));
                    delta.put(v, delta.get(v) + c);
                }
                if (!w.equals(s)) {
                    betweenness.put(w, betweenness.get(w) + delta.get(w));
                }
            }
        }

        return betweenness;
    }

    public void imprimirBetweennessCentrality() {
        Map<String, Double> betweenness = calcularBetweennessCentrality();

        for (Map.Entry<String, Double> entry : betweenness.entrySet()) {
            System.out.printf("Betweenness Centrality de %s: %.6f%n", entry.getKey(), entry.getValue());
        }
    }

    public void salvarBetweennessCentralityEmArquivo(String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            Map<String, Double> betweenness = calcularBetweennessCentrality();

            for (Map.Entry<String, Double> entry : betweenness.entrySet()) {
                writer.write(String.format("Betweenness Centrality de %s: %.6f%n", entry.getKey(), entry.getValue()));
            }

            System.out.println("Betweenness Centrality salva em: " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }


}
