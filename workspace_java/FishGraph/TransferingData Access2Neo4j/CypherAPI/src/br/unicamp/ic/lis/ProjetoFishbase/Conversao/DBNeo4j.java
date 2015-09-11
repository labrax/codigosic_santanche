package br.unicamp.ic.lis.ProjetoFishbase.Conversao;

public interface DBNeo4j {
    public void sendQuery(String query);
	
	public void createIndexes();

	public void createClass(String id, String name, String common_name);
	
	public void createOrder(String id, String name, String common_name, String class_id);
	
	public void createFamily(String id, String name, String common_name, String order_id);
	
	public void createGenus(String id, String name, String family_id);
	
	public void createSpeciesGen(String id, String name, String common_name, String genus_id);
	
	public void createSpeciesFam(String id, String name, String common_name, String fam_id);
	
	public void createCountry(String code, String name);
	
	public void createFAO(String code, String name);
	
	public void createEcosystem(String code, String name);
	
	public void relateCountryFAO(String C_CODE, String AreaCode);
	
	public void relateEcosystemCountry(String E_CODE, String C_CODE);
		
	public void relateEcosystemFAO(String E_CODE, String AreaCode);
	
	public void createPredator(String SpecCode, String PredatCode, String PredatorName, String PredatorI, String PredatorII, String PredatorGroup);
	
	public void createKey(String KeyCode, String OrdNum, String FamCode, String AreaCode, String C_CODE, String E_CODE);
	
	public void createKeyQuestion(String KeyCode, String OrdNum, String FamCode, String GenCode, String SpecCode);
	
	public void relateSpecieCountry(String C_CODE, String SpecCode);
	
	public void relateSpecieFAO(String AreaCode, String SpecCode);
	
	public void relateSpecieEcosystem(String E_CODE, String SpecCode);
}
