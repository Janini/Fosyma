package mas.agents;


public class InitAgent extends ExploAgent {
	//UN SEUL AGENT DE CE TYPE
	//CHARGE DE TRANSMETTRE UN POINT DE RENDEZ-VOUS (PAR DEFAUT SA POSITION INITIALE) À UN AGENT QUI PROPAGERA L'INFORMATION
	private static final long serialVersionUID = 1L;
	
	protected void setup(){
		super.setup();
		final Object[] args = getArguments();
		if(args[0]!=null){
			rdv = this.getCurrentPosition();
		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}

	}

	

}
