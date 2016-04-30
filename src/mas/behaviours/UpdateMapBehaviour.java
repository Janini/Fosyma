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
			List<Couple<String,List<Attribute>>> lobs = ag.observe();
			
			if(map.getNode(lastPos) != null){
				map.getNode(lastPos).removeAttribute("ui.class");
				if(ag.listTresors.containsKey(lastPos)){
					map.getNode(lastPos).setAttribute("ui.class", "treasure");
				}
			}
			if(map.getNode(pos)==null){ // Si la map ne contient pas deja le noeud, on l'ajoute
				//TODO GRID
				if (Principal.type.equals("Grid")){
					String[] split = pos.split("_");
					Node n = map.addNode(pos);
					
					if (split.length > 1){
						n.addAttribute("x", split[0]);
						n.addAttribute("y", split[1]);
						n.addAttribute("ui.label", split[0]+","+split[1]);
						n.addAttribute("ui.class", "agent");
					}					
					
				}
				//TODO AUTRES
				if (Principal.type.equals("mapCouloir")){
					Node n = map.addNode(pos);
					System.out.println("CREATION "+pos);
					n.addAttribute("x", pos);
					n.addAttribute("ui.label",pos);
					n.addAttribute("ui.class", "agent");
				}
				
				try{
					Thread.sleep(100);
				} catch(Exception e){
					
				}
			}
			else{
				map.getNode(pos).setAttribute("ui.class", "agent");
			}
			MoveBehaviour.remove(lobs,ag.getVus());
			
			for(Couple<String,List<Attribute>> c : lobs){
				String pos2 = c.getLeft();
				System.out.println(pos2);
				if(pos2.equals(pos))
					continue;
				String[] split2 = pos2.split("_");
				if(map.getNode(pos2) == null){
					if (Principal.type.equals("Grid")){
						Node n2 = Principal.graph.addNode(pos2);
						n2.addAttribute("x", split2[0]);
						n2.addAttribute("y", split2[1]);
						n2.addAttribute("ui.label", split2[0]+","+split2[1]);
						n2.setAttribute("ui.class", "unexplored");
						Principal.graph.addEdge(pos+"-"+pos2, pos, pos2);
					}
					
					if (Principal.type.equals("mapCouloir")){
						Node n = Principal.graph.addNode(pos2);
						n.addAttribute("x", pos2);
						n.addAttribute("ui.label", pos2);
						n.setAttribute("ui.class", "unexplored");
						Principal.graph.addEdge(pos+"-"+pos2, pos, pos2);
					}
					
					try{
						Thread.sleep(100);
					} catch(Exception e){
						
					}
				
				}
				if(map.getEdge(pos+"-"+pos2)==null && map.getEdge(pos2+"-"+pos)==null && pos2!=pos){
					Principal.graph.addEdge(pos+"-"+pos2, pos, pos2);					
				}
			}
			
			/*if(lastPos!=""){ // Si il existe une derniere position connue
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
						
						
						
						
					}
//					try {
//						List<Couple<String,List<Attribute>>> lobs = ag.observe();
//						if (!lobs.isEmpty()){
//							synchronized(ExploAgent.lock){
//								for (int i = 1; i< lobs.size(); i++){
//									String salle = lobs.get(i).getLeft();
//									if (map.getNode(salle) != null && map.getEdge(pos+"-"+salle) == null && map.getEdge(salle + "-" + pos) == null){
//										map.addEdge(pos+"-"+salle, salle, pos); // Nouvelle arete
//									}
//								}
//							}
//						}
//						try{
//							Thread.sleep(200);
//						} catch(Exception e){
//							
//						}
						map.addEdge(pos+"-"+lastPos, lastPos, pos); // Nouvelle arete
//					} catch (ElementNotFoundException e) {
//						System.err.println("#Null Pointer - UpdateMap");
//						finished = true;
//					}
				}
			}
					//myAgent.addBehaviour(new SayHelloBehaviour(ag));

			*/
			finished = true;

		}

	@Override
	public boolean done() {
		return finished;
	}

}
