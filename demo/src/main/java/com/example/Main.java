package com.example;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String caminhoArquivo = "demo/src/main/java/com/example/LesMiserables.gexf"; 
        Grafo G = new Grafo();
        G.carregarGrafo(caminhoArquivo);
        G.imprimirGrafo();
    }
    
}