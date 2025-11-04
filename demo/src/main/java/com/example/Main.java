package com.example;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String caminhoArquivo = "demo/src/main/java/com/example/LesMiserables.gexf"; 
        String caminhoArquivoBC = "demo/src/main/java/com/example/LesMiserablesBetweenness.txt";

        Grafo G = new Grafo();
        G.carregarGrafo(caminhoArquivo);
        G.imprimirBetweennessCentrality();
        G.salvarBetweennessCentralityEmArquivo(caminhoArquivoBC);
    }
    
}