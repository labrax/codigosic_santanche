package br.unicamp.ic.lis.ProjetoFishbase.Conversao;

import java.sql.ResultSet;

public class Main {
	ConnectorAccess connAcc = null;
	DBNeo4j db = null;
	
	// numbering for displayed information
	Integer number_classes = 0;
	Integer number_orders = 0;
	Integer number_families = 0;
	Integer number_genera = 0;
	Integer number_species = 0;
	
	Integer number_countries = 0;
	Integer number_faos = 0;
	Integer number_ecosystems = 0;
	
	Integer number_relations_countfao = 0;
	Integer number_relations_ecosystemcountry = 0;
	Integer number_relations_ecosystemfao = 0;
	
	Integer number_predats = 0;
	
	Integer number_species_country = 0;
	Integer number_species_FAO = 0;
	Integer number_species_ecosystem = 0;
	
	Integer number_keys = 0;
	Integer number_keyquestions = 0;
	
	public static void main(String [] args) {
		Main a = new Main();
		
		System.out.println("Starting...");
		
		long start = System.currentTimeMillis();
		try {
			a.start();
			// transfer all taxonomic information, from classes to species
			a.species_to_classes();
			// transfer all area informations, countries, faos and ecosystems
			a.areas_fao();
			// transfer predators information
			a.predats();
			// transfer relations of species and areas
			a.species_to_places();
			// transfer information of keys
			a.keys_to_all();
			
			
			// test neo4j node structure
			//a.test();
			a.finish();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		long end = System.currentTimeMillis();

		System.out.println("Finished after " + ((end - start) / 1000d) + " seconds.");
	}
	
	public void start() throws Exception {
		db = new DBNeo4jBatchInserter217();
		System.out.println("Creating indexes...");
		db.createIndexes();
		
		System.out.println("Loading Access DB...");
		connAcc = new ConnectorAccess();
	}
	
	public void finish() {
		db.shutdown();
	}
	
	// tests for neo4j structure and node duplicity
	public void test() {
		//testa a cria��o class-genus
		db.createClass("1", "Classe", "Classe para todos os peixes");
		db.flush_index();
		db.createOrder("2", "Ordem", "Ordeim", "1");
		db.flush_index();
		db.createFamily("3", "Fam�lia", "Familiaum", "2");
		db.flush_index();
		db.createGenus("4", "Frutas", "3");
		db.flush_index();
		
		//cria��o de esp�cie com base em fam�lia
		db.createSpeciesFam("123", "Peixe Porco 1", "Pr�tico", "3"); //os 3 peixes porquinhos
		db.createSpeciesFam("223", "Peixe Porco 2", "Heitor", "3");
		db.createSpeciesFam("225", "Peixe Porco 3", "C�cero", "3");
		db.flush_index();
		
		//testa a cria��o de esp�cie com base em g�nero
		db.createSpeciesGen("999", "Peixe Ma�a Isca", "Isca", "4");
		db.flush_index();
		
		//testa a cria��o de locais 
		db.createCountry("CASA BEM RUIM", "Casa de cola, bambus e barbantes");
		db.createEcosystem("CASA RUIM", "Casa de madeira");
		db.createFAO("CASA BOA", "Casa s�lida, feita de tijolos e cimento");
		db.flush_index();
		
		//testa o relacionamento de locais com esp�cies
		db.relateSpecieCountry("CASA BEM RUIM", "123");
		db.relateSpecieEcosystem("CASA RUIM", "223");
		db.relateSpecieFAO("CASA BOA", "225");
		
		//testa o relacionamento entre locais
		db.relateEcosystemCountry("CASA RUIM", "CASA BEM RUIM");
		db.relateCountryFAO("CASA BEM RUIM", "CASA BOA");
		db.relateEcosystemFAO("CASA RUIM", "CASA BOA");
		
		//testa a cria��o de chaves
		db.createKey("1", "2", "3", "CASA BOA", "CASA BEM RUIM", "CASA RUIM");
		db.createKey("2", "", "", "", "", "");
		db.createKeyQuestion("4", "2", "3", "4", "999");
		db.createKeyQuestion("5", "", "", "", "");
		db.flush_index();
		
		//testa a re-cria��o de chaves //n�o deveria ser feito outros n�s!
		db.createKey("1", "2", "3", "CASA BOA", "CASA BEM RUIM", "CASA RUIM");
		db.createKey("2", "", "", "", "", "");
		db.createKeyQuestion("4", "2", "3", "4", "999");
		db.createKeyQuestion("5", "", "", "", "");
		db.flush_index();
		
		//testa a cria��o de predador com o mesmo c�digo
		db.createPredator("123", "001", "Peixe Lobo", "Mau", "Sucesso para destruir a casa", "Lobos");
		db.createPredator("223", "001", "Peixe Lobo", "Mau", "Sucesso para destruir a casa", "Lobos");
		db.flush_index();
		
		//testes adicionais
		db.createFAO("LOCAL", "PERTO DA CASA BOA");
		db.relateSpecieFAO("LOCAL", "999");

		//testa a cria��o de predador sem c�digo
		db.createPredator("999", "", "Peixe Lobo", "Mau", "N�o conseguiu", "Lobos");
		db.flush_index();
	}
	
	public void species_to_classes() {
		try {
			// query result
			ResultSet rs;
			
			// amount of rows on table
			rs = connAcc.query("SELECT COUNT (*) FROM CLASSES");
			rs.next();
			number_classes = Integer.parseInt(rs.getString(1));
			
			rs = connAcc.query("SELECT COUNT (*) FROM ORDERS");
			rs.next();
			number_orders = Integer.parseInt(rs.getString(1));
			rs = connAcc.query("SELECT COUNT (*) FROM FAMILIES");
			rs.next();
			number_families = Integer.parseInt(rs.getString(1));
			rs = connAcc.query("SELECT COUNT (*) FROM GENERA");
			rs.next();
			number_genera = Integer.parseInt(rs.getString(1));
			rs = connAcc.query("SELECT COUNT (*) FROM SPECIES");
			rs.next();
			number_species = Integer.parseInt(rs.getString(1));
			
			// to display information
			Integer count;
			//-----------ADD CLASSES BLOCK
			count = 0;
			System.out.println("Adding classes!");
			
			//access query
			rs = connAcc.query("SELECT [ClassNum], [Class], [CommonName] FROM [CLASSES]");
			
			while (rs.next()) {
				// send to cypher
				db.createClass(rs.getString(1), rs.getString(2), rs.getString(3));
				
				// display information
				count++;
				if(count == number_classes || count%10 == 0)
					System.out.print(String.format("Classes [%s/%s]", count, number_classes)+"\r");
			}
			
			db.flush_index();
			//-----------END ADD CLASSES BLOCK
			
			//-----------ADD ORDERS BLOCK
			count = 0;
			System.out.println("Adicionando ordens!");
			rs = connAcc.query("SELECT [Ordnum], [Order], [CommonName], [ClassNum] FROM [ORDERS]");
			
			while (rs.next()) {
				db.createOrder(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
				count++;
				if(count == number_orders || count%10 == 0)
					System.out.print(String.format("Orders [%s/%s]", count, number_orders)+"\r");
			}
			db.flush_index();
			//-----------END ADD ORDERS BLOCK
			//-----------ADD FAMILIES BLOCK
			count = 0;
			System.out.println("Adicionando fam�lias!");
			rs = connAcc.query("SELECT [FamCode], [Family], [CommonName], [Ordnum] FROM [FAMILIES]");
			
			while (rs.next()) {
				db.createFamily(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
				count++;
				if(count == number_families || count%10 == 0)
					System.out.print(String.format("Families [%s/%s]", count, number_families)+"\r");
			}
			db.flush_index();
			//-----------END ADD FAMILIES BLOCK
			//-----------ADD GENERA BLOCK
			count = 0;
			System.out.println("Adicionando g�neros!");
			rs = connAcc.query("SELECT [GenCode], [GenName], [FamCode] FROM [GENERA]");
			
			while (rs.next()) {
				db.createGenus(rs.getString(1), rs.getString(2), rs.getString(3));
				count++;
				if(count == number_genera || count%10 == 0)
					System.out.print(String.format("Genera [%s/%s]", count, number_genera)+"\r");
			}
			db.flush_index();
			//-----------END ADD GENERA BLOCK
			//-----------ADD SPECIES BLOCK
			count = 0;
			System.out.println("Adicionando esp�cies!");
			rs = connAcc.query("SELECT [SpecCode], [Species], [FBname], [GenCode], [FamCode] FROM [SPECIES]");
			
			while (rs.next()) {
				if(rs.getString(4) != "")
					db.createSpeciesGen(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
				else {
					System.out.println("|");
					db.createSpeciesFam(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(5));
				}
				count++;
				if(count == number_species || count%10 == 0)
					System.out.print(String.format("Species [%s/%s]", count, number_species)+"\r");
			}
			db.flush_index();
			//-----------END ADD SPECIES BLOCK
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void areas_fao() {
		try {
			ResultSet rs;
			
			rs = connAcc.query("SELECT COUNT (*) FROM COUNTREF");
			rs.next();
			number_countries = Integer.parseInt(rs.getString(1));
			rs = connAcc.query("SELECT COUNT (*) FROM FAOARREF");
			rs.next();
			number_faos = Integer.parseInt(rs.getString(1));
			rs = connAcc.query("SELECT COUNT (*) FROM ECOSYSTEMREF");
			rs.next();
			number_ecosystems = Integer.parseInt(rs.getString(1));
			
			rs = connAcc.query("SELECT COUNT (*) FROM COUNTFAOREF");
			rs.next();
			number_relations_countfao = Integer.parseInt(rs.getString(1));
			rs = connAcc.query("SELECT COUNT (*) FROM ECOSYSTEMCNTREF");
			rs.next();
			number_relations_ecosystemcountry = Integer.parseInt(rs.getString(1));
			rs = connAcc.query("SELECT COUNT (*) FROM ECOSYSTEMFAOREF");
			rs.next();
			number_relations_ecosystemfao = Integer.parseInt(rs.getString(1));
			
			Integer count;
			//-----------ADD COUNTRIES BLOCK
			count = 0;
			System.out.println("Adicionando pa�ses!");
			rs = connAcc.query("SELECT [C_Code], [PAESE] FROM [COUNTREF]");
			
			while (rs.next()) {
				db.createCountry(rs.getString(1), rs.getString(2));
				count++;
				if(count == number_countries || count%10 == 0)
					System.out.print(String.format("Countries [%s/%s]", count, number_countries)+"\r");
			}
			db.flush_index();
			//-----------END ADD COUNTRIES BLOCK
			//-----------ADD FAOS BLOCK
			count = 0;
			System.out.println("Adicionando FAOs!");
			rs = connAcc.query("SELECT [AreaCode], [FAO] FROM [FAOARREF]");
			
			while (rs.next()) {
				db.createFAO(rs.getString(1), rs.getString(2));
				count++;
				if(count == number_faos || count%10 == 0)
					System.out.print(String.format("FAOs [%s/%s]", count, number_faos)+"\r");
			}
			db.flush_index();
			//-----------END ADD FAOS BLOCK
			//-----------ADD ECOSYSTEM BLOCK
			count = 0;
			System.out.println("Adicionando Ecosistemas!");
			rs = connAcc.query("SELECT [E_CODE], [EcosystemName] FROM [ECOSYSTEMREF]");
			
			while (rs.next()) {
				db.createEcosystem(rs.getString(1), rs.getString(2));
				count++;
				if(count == number_ecosystems || count%10 == 0)
					System.out.print(String.format("Ecosystems [%s/%s]", count, number_ecosystems)+"\r");
			}
			db.flush_index();
			//-----------END ADD ECOSYSTEM BLOCK
			
			//-----------ADD COUNTRYFAO BLOCK
			count = 0;
			System.out.println("Adicionando rela��es pa�ses-FAOs!");
			rs = connAcc.query("SELECT [C_CODE], [AreaCode] FROM [COUNTFAOREF]");
			
			while (rs.next()) {
				db.relateCountryFAO(rs.getString(1), rs.getString(2));
				count++;
				if(count == number_relations_countfao || count%10 == 0)
					System.out.print(String.format("Relations Country-FAOs [%s/%s]", count, number_relations_countfao)+"\r");
			}
			//-----------END ADD COUNTRYFAO BLOCK
			//-----------ADD ECOSYSTEMCOUNTRY BLOCK
			count = 0;
			System.out.println("Adicionando rela��es ecosistemas-pa�ses!");
			rs = connAcc.query("SELECT [E_CODE], [C_CODE] FROM [ECOSYSTEMCNTREF]");
			
			while (rs.next()) {
				db.relateEcosystemCountry(rs.getString(1), rs.getString(2));
				count++;
				if(count == number_relations_ecosystemcountry || count%10 == 0)
					System.out.print(String.format("Relations Ecosystem-Countries [%s/%s]", count, number_relations_ecosystemcountry)+"\r");
			}
			//-----------END ADD ECOSYSTEMCOUNTRY BLOCK
			//-----------ADD ECOSYSTEMFAO BLOCK
			count = 0;
			System.out.println("Adicionando rela��es ecosistemas-FAOs!");
			rs = connAcc.query("SELECT [E_CODE], [AreaCode] FROM [ECOSYSTEMFAOREF]");
			
			while (rs.next()) {
				db.relateEcosystemFAO(rs.getString(1), rs.getString(2));
				count++;
				if(count == number_relations_ecosystemfao || count%10 == 0)
					System.out.print(String.format("Relations Ecosystem-FAOs [%s/%s]", count, number_relations_ecosystemfao)+"\r");
			}
			//-----------END ADD ECOSYSTEMFAO BLOCK
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void predats() {
		try {
			ResultSet rs;
			
			rs = connAcc.query("SELECT COUNT (*) FROM PREDATS");
			rs.next();
			number_predats = Integer.parseInt(rs.getString(1));
			
			Integer count;
			//-----------ADD PREDATS BLOCK
			count = 0;
			System.out.println("Adicionando predadores!");
			rs = connAcc.query("SELECT [SpecCode], [PredatCode], [PredatorName], [PredatorI], [PredatorII], [PredatorGroup] FROM [PREDATS]");
			
			while (rs.next()) {
				db.createPredator(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
				count++;
				if(count == number_predats || count%10 == 0)
					System.out.print(String.format("Predators [%s/%s]", count, number_predats)+"\r");
			}
			db.flush_index();
			//-----------END ADD PREDATS BLOCK

		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void species_to_places() {
		try {
			ResultSet rs;
			
			rs = connAcc.query("SELECT COUNT (*) FROM COUNTRY");
			rs.next();
			number_species_country = Integer.parseInt(rs.getString(1));
			
			rs = connAcc.query("SELECT COUNT (*) FROM COUNTFAO");
			rs.next();
			number_species_FAO = Integer.parseInt(rs.getString(1));
			
			rs = connAcc.query("SELECT COUNT (*) FROM ECOSYSTEMCOUNTRY");
			rs.next();
			number_species_ecosystem = Integer.parseInt(rs.getString(1));
			
			Integer count;
			
			//-----------ADD COUNTRY_SPECIES BLOCK
			count = 0;
			System.out.println("Adicionando rela��es pa�s-esp�cies!");
			rs = connAcc.query("SELECT [C_CODE], [SpecCode] FROM [COUNTRY]");
			
			while (rs.next()) {
				db.relateSpecieCountry(rs.getString(1), rs.getString(2));
				count++;
				if(count == number_species_country || count%10 == 0)
					System.out.print(String.format("Country-Species [%s/%s]", count, number_species_country)+"\r");
			}
			//-----------END ADD COUNTRY_SPECIES BLOCK
			
			
			//-----------ADD FAO_SPECIES BLOCK
			count = 0;
			System.out.println("Adicionando rela��es FAOs-esp�cies!");
			rs = connAcc.query("SELECT [AreaCode], [SpecCode] FROM [COUNTFAO]");
			
			while (rs.next()) {
				db.relateSpecieFAO(rs.getString(1), rs.getString(2));
				count++;
				if(count == number_species_FAO || count%10 == 0)
					System.out.print(String.format("FAO-Species [%s/%s]", count, number_species_FAO)+"\r");
			}
			//-----------END ADD FAO_SPECIES BLOCK
			
			
			//-----------ADD ECOSYSTEM_SPECIES BLOCK
			count = 0;
			System.out.println("Adicionando rela��es ecosistemas-esp�cies!");
			rs = connAcc.query("SELECT [E_CODE], [Speccode] FROM [ECOSYSTEMCOUNTRY]");
			
			while (rs.next()) {
				db.relateSpecieEcosystem(rs.getString(1), rs.getString(2));
				count++;
				if(count == number_species_ecosystem || count%10 == 0)
					System.out.print(String.format("Ecosystem-Species [%s/%s]", count, number_species_ecosystem)+"\r");
			}
			//-----------END ADD ECOSYSTEM_SPECIES BLOCK 
			

		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	void keys_to_all() {
		try {
			ResultSet rs;
			
			rs = connAcc.query("SELECT COUNT (*) FROM KEYS");
			rs.next();
			number_keys = Integer.parseInt(rs.getString(1));
			
			rs = connAcc.query("SELECT COUNT (*) FROM KEYQUESTIONS");
			rs.next();
			number_keyquestions = Integer.parseInt(rs.getString(1));
			
			Integer count;
			//-----------ADD KEYS BLOCK
			count = 0;
			System.out.println("Adicionando rela��es de chave!");
			rs = connAcc.query("SELECT [KeyCode], [OrdNum], [FamCode], [AreaCode], [C_CODE], [E_CODE] FROM [KEYS]");
			
			while (rs.next()) {
				db.createKey(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
				count++;
				if(count == number_keys || count%10 == 0)
					System.out.print(String.format("Keys [%s/%s]", count, number_keys)+"\r");
			}
			db.flush_index();
			//-----------END ADD KEYS BLOCK
			//-----------ADD KEYQUESTIONS BLOCK
			count = 0;
			System.out.println("Adicionando rela��es de chave-quest�es!");
			rs = connAcc.query("SELECT [KeyCode], [OrdNum], [FamCode], [GenCode], [SpecCode] FROM [KEYQUESTIONS]");
			
			while (rs.next()) {
				db.createKeyQuestion(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
				count++;
				if(count == number_keyquestions || count%10 == 0)
					System.out.print(String.format("Key-Questions [%s/%s]", count, number_keyquestions)+"\r");
			}
			db.flush_index();
			//-----------END ADD KEYQUESTIONS BLOCK

		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
