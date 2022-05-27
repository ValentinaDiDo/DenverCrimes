package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	/*I VERTICI SONO I TIPI DI REATO DIVERSI TRA LORO E GLI ARCHI HANNO UN PESO OARI AL NUMERO DI QUARTIERI DISTINTI IN CUI SI 
	 * SONO VERIFICATI ENTRAMBI I TIPI DI REATO
	 * I VERTICI NON SONO GLI EVENTI, MA SONO I TIPI DI REATO !!*/
	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	List<String> best;
	
	public Model() {
		this.dao = new EventsDao();
	}
	public void creaGrafo(String categoria, int mese) {
		//PASSO IL MESE COME INTERO, POI NELLA QUEERY USO LA FUNZIONE MONTH DI MYSQL 
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		
		//AGGIUNTA VERTICI : SI PUO OMETTERE PERCHE' LI AGGIUNGO DOPO 
		//Graphs.addAllVertices(this.grafo, dao.getVertici(mese, categoria));
		
		//AGGIUNTA ARCHI: DEVO CALCOLARE IL PESO
		
		for(Adiacenza a : this.dao.getArchi(categoria, mese)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
		}
		System.out.println("GRAFO CREATO\n# NUMERO VERTICI: "+this.grafo.vertexSet().size()+"\n# ARCHI: "+this.grafo.edgeSet().size());
	}
	
	public List<Adiacenza> getArchiMaggioriPesoMedio(){
		int somma = 0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			somma += this.grafo.getEdgeWeight(e);
		}
		double avg = somma / this.grafo.edgeSet().size();
		
		List<Adiacenza> result = new ArrayList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>avg)
				result.add(new Adiacenza(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), (int) grafo.getEdgeWeight(e)));
		}
		return result;
	}
	
	public List<String> calcolaPercorso(String sorgente, String destinazione){
		best = new LinkedList<>();
		List<String> parziale = new LinkedList<>();
		parziale.add(sorgente);
		cerca(parziale, destinazione);
		return best;
	}
	private void cerca(List<String> parziale, String destinazione) {
		//RICERCA RICORSIVA
		
		//CONDIZIONE DI TERMINAZIONE : QUANDO ARRIVO A DESTINAZIONE
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			//SONO ARRIVATO A DESTINAZIONE -> CONTROLLO SE LA SOLUZIONE E' MIGLIORE
			if(parziale.size()>best.size()) {
				best = new LinkedList<>(parziale);
				return; //ESCO DALLA RICORSIONE
			}
		}
		//METODI RICORSIVI : ESPLORO I PERCORSI CHE VANNO VERSO GLI ADIACENTI
		
		for(String s : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))){
			
			if(!parziale.contains(s)) { //NON DEVE CONTENERE GIA' IL VERTICE PERCHE' NON VOGLIO UN CAMMINO CICLICO 
				parziale.add(s);
				cerca(parziale, destinazione);
				parziale.remove(parziale.size()-1); //	BACKTRACKING
				}	
			}
		
	}
	
	
	
	
	
	
	
	
	
	
}
 