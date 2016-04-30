package mas.behaviours;


import java.util.List;

import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import mas.agents.ExploAgent;
import mas.agents.ExploAgentBis;

import org.graphstream.graph.Edge;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;

import env.Attribute;
import env.Couple;
import princ.Principal;

public class UpdateMapBehaviour extends SimpleBehaviour{

	static int id = 0;
	private static final long serialVersionUID = 9088212802507795289L;
	private boolean finished = false;

	//TODO CONSTRUCTEUR POUR MIGRATION (AGENTBIS)
	/*public UpdateMapBehaviour(ExploAgentBis ag) {
		super(ag,100);
	}*/
	
	public UpdateMapBehaviour(ExploAgent ag) {
		super(ag);
	}

	@Override
	public void action() {
		
//			System.out.println("***********UpdateMapBehaviour**********");
	
			ExploAgent ag = (ExploAgent)this.myAgent;
			if (ag.block || ag.interblocage){
				System.out.println(ag.getLocalName()+ " : Je suis bloqué ");
				return;
			}
			String pos = "";
			try {
				pos = ag.getCurrentPosition();
			} catch (NullPointerException e) {
				System.err.println("#Null Pointer - UpdateMap");
				return;
			}
			String lastPos = ag.getLastPos();
			
			if(ag.getPath().size()!=0){
				lastPos = (ag.getPath().get(ag.getPath().size()-1)); // On recupere la derniere position
			}
			
//			System.out.println("Agent : "+ myAgent.getLocalName()+" "+ag.getPath());
//			System.out.println("Agent : "+ myAgent.getLocalName()+" Position " + pos);
//			System.out.println("Agent : "+ myAgent.getLocalName()+" LastPosition " + lastPos);
//			
			
			Graph map = Principal.graph;
			if(map.getNode(pos)==null){ // Si la map ne contient pas deja le noeud, on l'ajoute
				//TODO GRID
				if (Principal.type.equals("Grid")){
					String[] split = pos.split("_");
					Node n = map.addNode(pos);
					
					if (split.length > 1){
						n.addAttribute("x", split[0]);
						n.addAttribute("y", split[1]);
						n.addAttribute("ui.label", split[0]+","+split[1]);
					}
					
					
				}
				//TODO AUTRES
				if (Principal.type.equals("mapCouloir")){
					Node n = map.addNode(pos);
					System.out.println("CREATION "+pos);
					n.addAttribute("x", pos);
					n.addAttribute("ui.label",pos);
				}
				
				try{
					Thread.sleep(100);
				} catch(Exception e){
					
				}
			}
			if(lastPos!=""){ // Si il existe une derniere position connue
				if(map.getEdge(pos+"-"+lastPos)==null && map.getEdge(lastPos+"-"+pos)==null && lastPos!=pos){ // Si l'arete n'existe pas deja et que lastPos != pos actuelle
					if(map.getNode(lastPos) == null){
					
						//TODO GRID
						if (Principal.type.equals("Grid")){
							String[] split = lastPos.split("_");
							Node n = Principal.graph.addNode(lastPos);
							
							n.addAttribute("x", split[0]);
							n.addAttribute("y", split[1]);
								n.addAttribute("ui.label", split[0]+","+split[1]);
						}
						//TODO AUTRES
						if (Principal.type.equals("mapCouloir")){
							Node n = Principal.graph.addNode(lastPos);
							n.addAttribute("x", lastPos);
							n.addAttribute("ui.label", lastPos);
						}
						
						try{
							Thread.sleep(100);
						} catch(Exception e){
							
						}
					}
					try {
						List<Couple<String,List<Attribute>>> lobs = ag.observe();
						if (!lobs.isEmpty())
							for (int i = 1; i< lobs.size(); i++){
								String salle = lobs.get(i).getLeft();
								synchronized(ExploAgent.lock){
									if (map.getNode(salle) != null && map.getEdge(pos+"-"+salle) == null && map.getEdge(salle + "-" + pos) == null){
										map.addEdge(pos+"-"+salle, salle, pos); // Nouvelle arete
									}
								}
							}
						//map.addEdge(pos+"-"+lastPos, lastPos, pos); // Nouvelle arete
					} catch (ElementNotFoundException e) {
						System.err.println("#Null Pointer - UpdateMap");
						finished = true;
					}
				}
			}
					//myAgent.addBehaviour(new SayHelloBehaviour(ag));
			finished = true;

		}

	@Override
	public boolean done() {
		return finished;
	}

}
