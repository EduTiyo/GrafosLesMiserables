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

    public void imprimirEccentricity(){
        // A excentricidade de um vértice é a maior distância entre ele e qualquer outro vértice no grafo.
        // Implementado usando BFS para encontrar a distância minima entre os vértices e pegar a maior de todas, encontrando assim a excentricidade.

        for (String vertice : adjacencia.keySet()) {
            int excentricidade = calcularExcentricidade(vertice);
            System.out.println("Excentricidade de " + vertice + ": " + excentricidade);
        }
    }

    private int calcularExcentricidade(String inicio) { 
        Queue<String> fila = new LinkedList<>();
        Map<String, Integer> distancias = new HashMap<>();
        Set<String> visitados = new HashSet<>();

        fila.add(inicio);
        distancias.put(inicio, 0);
        visitados.add(inicio);

        while (!fila.isEmpty()) {
            String verticeAtual = fila.poll();
            int distanciaAtual = distancias.get(verticeAtual);

            for (String vizinho : adjacencia.get(verticeAtual)) {
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    distancias.put(vizinho, distanciaAtual + 1);
                    fila.add(vizinho);
                }
            }
        }

        return Collections.max(distancias.values());
    }
    
    public void salvarEccentricityEmArquivo(String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (String vertice : adjacencia.keySet()) {
                int excentricidade = calcularExcentricidade(vertice);
                String linha = "Excentricidade de " + vertice + ": " + excentricidade;
                writer.write(linha);
                writer.newLine();
            }
            System.out.println("Excentricidades salvas em: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }


    /*
      A Closeness Centrality mede o quão "central" um vértice é em relação aos outros —
      isto é, o quão próximo ele está de todos os demais, com base na distância média
      até cada vértice alcançável.
      
      A fórmula normalizada é:
      CC(v) = (N - 1) / sum(d(v,u)), onde d(v,u) é a distância mínima entre v e u.
      Caso o vértice não consiga alcançar todos os outros, o valor retornado é 0.
    */
    public void imprimirClosenessCentrality() {
        Map<String, Double> centralidades = calcularClosenessCentrality();

        for (Map.Entry<String, Double> entry : centralidades.entrySet()) {
            System.out.printf("Closeness Centrality de %s: %.6f%n", entry.getKey(), entry.getValue());
        }
    }

    public Map<String, Double> calcularClosenessCentrality() {
        Map<String, Double> centralidades = new HashMap<>();
        int N = adjacencia.size();

        for (String vertice : adjacencia.keySet()) {
            Map<String, Integer> distancias = bfsDistancias(vertice);
            int alcançaveis = distancias.size();

            if (alcançaveis < N) {
                centralidades.put(vertice, 0.0);
                continue;
            }

            int somaDistancias = distancias.values().stream().mapToInt(Integer::intValue).sum();
            double ccNormalizada = (double) (N - 1) / somaDistancias;
            centralidades.put(vertice, ccNormalizada);
        }

        return centralidades;
    }

    public void salvarClosenessCentralityEmArquivo(String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            Map<String, Double> centralidades = calcularClosenessCentrality();

            for (Map.Entry<String, Double> entry : centralidades.entrySet()) {
                writer.write(String.format("Closeness Centrality de %s: %.6f%n", entry.getKey(), entry.getValue()));
            }

            System.out.println("Closeness Centrality salva em: " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private Map<String, Integer> bfsDistancias(String inicio) {
        Queue<String> fila = new LinkedList<>();
        Map<String, Integer> distancias = new HashMap<>();
        Set<String> visitados = new HashSet<>();

        fila.add(inicio);
        distancias.put(inicio, 0);
        visitados.add(inicio);

        while (!fila.isEmpty()) {
            String verticeAtual = fila.poll();
            int distanciaAtual = distancias.get(verticeAtual);

            for (String vizinho : adjacencia.get(verticeAtual)) {
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    distancias.put(vizinho, distanciaAtual + 1);
                    fila.add(vizinho);
                }
            }
        }

        return distancias;
    }

    


}
