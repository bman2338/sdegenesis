package ch.usi.inf.genesis.data.repository;

public enum FamixLanguage{
	JAVA(0),
	C(1),
	CPP(2);

	private final int id;

	FamixLanguage(final int id){
		this.id = id;
	}

	public String getId(){
		switch(id){
		case 1:
			return "c";
		case 2:
			return "cpp";
		}

		return "java";
	}

}
