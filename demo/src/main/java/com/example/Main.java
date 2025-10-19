package com.example;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String caminhoArquivo = "demo/src/main/java/com/example/LesMiserables.gexf"; 
        String caminhoArquivoExcentricidade = "demo/src/main/java/com/example/LesMiserablesEccentricity.txt";
        String caminhoArquivoCC = "demo/src/main/java/com/example/LesMiserablesCloseness.txt";
        
        Grafo G = new Grafo();
        G.carregarGrafo(caminhoArquivo);
        //G.imprimirGrafo();
        G.imprimirEccentricity();
        G.salvarEccentricityEmArquivo(caminhoArquivoExcentricidade);

        G.imprimirClosenessCentrality();
        G.salvarClosenessCentralityEmArquivo(caminhoArquivoCC);
    }
    
}