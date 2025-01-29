package main;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import miningChanges.CorpusCreation;
import miningChanges.ProduceChangeLevelResults;
import miningChanges.ProduceFileLevelResults;
import preprocess.ExtractCommits;
import utils.FileToLines;

public class Main {

	public static HashMap<String,String> settings;
	public static String task = "";
	public static String repoDir = "";
	public static String workingLoc = "";
	public static String bugReport = "";
	public static String changeOracle = "";
	public static String sourceDir = "";
	public static String projectName = "";
	public static String versionName = "";
	public static double lambda = 5;
	public static double belta1 = 0.1;
	public static double belta2 = 0.2;
	public static void startTask() throws Exception {
		//make outputFile path.
		File dir = new File(workingLoc);
		if (!dir.exists())
			dir.mkdirs();

		if (task.equals("indexHunks")) {
			ExtractCommits.indexHunks();
		}
		else if (task.equals("corpusCreation")) {
			// Create the corpus change logs, hunks, and create the code like term corpus
			CorpusCreation.createCorpus();
		}
		else if (task.equals("produceChangeResults")) {
			ProduceChangeLevelResults rank = new ProduceChangeLevelResults();
			rank.getFinalResults();
		}
		else if (task.equals("produceFileResults")) {
			ProduceFileLevelResults rank = new ProduceFileLevelResults();
			rank.getFinalResults();
		}
		else if (task.equals("all")) {
			ExtractCommits.indexHunks();
			System.out.println("Finish Indexing Files");
			CorpusCreation.createCorpus();
			System.out.println("Finish Creating Corpus");
			ProduceChangeLevelResults rank1 = new ProduceChangeLevelResults();
			rank1.getFinalResults();
			System.out.println("Finish Creating Change Level Results");
			ProduceFileLevelResults rank2 = new ProduceFileLevelResults();
			rank2.getFinalResults();
			System.out.println("Finish Creating File Level Results");
		}
		else if (task.equals("fileall")) {
			ExtractCommits.indexHunks();
			System.out.println("Finish Indexing Files");
			if (CorpusCreation.createCorpus()==false){
				System.err.println("Failed to create corpus!");
				return;
			}
			else
				System.out.println("Finish Creating Corpus");
			ProduceFileLevelResults rank2 = new ProduceFileLevelResults();
			rank2.getFinalResults();
			System.out.println("Finish Creating File Level Results");
		}
	}

	// Parse command-line arguments based on flags
	public static void parseArguments(String[] args) {
		settings = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "-t": // task
					if (i + 1 < args.length) task = args[++i];
					settings.put("task", task);
					break;
				case "-r": // repoDir
					if (i + 1 < args.length) repoDir = args[++i];
					settings.put("repoDir", repoDir);
					break;
				case "-w": // workingLoc
					if (i + 1 < args.length) workingLoc = args[++i];
					settings.put("workingLoc", workingLoc);
					break;
				case "-b": // bugReport
					if (i + 1 < args.length) bugReport = args[++i];
					settings.put("bugReport", bugReport);
					break;
				case "-c": // changeOracle
					if (i + 1 < args.length) changeOracle = args[++i];
					settings.put("changeOracle", changeOracle);
					break;
				case "-s": // sourceDir
					if (i + 1 < args.length) sourceDir = args[++i];
					settings.put("sourceDir", sourceDir);
					break;
				case "-p": // projectName
					if (i + 1 < args.length) projectName = args[++i];
					break;
				case "-v": // versionName
					if (i + 1 < args.length) versionName = args[++i];
					break;
				default:
					System.err.println("Unknown flag: " + args[i]);
					break;
			}
			settings.put("Project", projectName);
			settings.put("Version", versionName);
			settings.put("lambda", Double.toString(lambda));
			settings.put("belta1", Double.toString(belta1));
			settings.put("belta2", Double.toString(belta2));
		}


	}

	public static boolean loadConfigure(String filename) throws Exception {
		File file = new File(filename);
		settings = new HashMap<String,String>();
		if (!file.exists()) {
			System.out.println("Configuration File Not Found!");
			return false;
		}
		else {
			List<String> lines = FileToLines.fileToLines(filename);
			for (String line : lines) {
				if (!line.startsWith("#") && line.contains("=")) {
					settings.put(line.split("=")[0].trim(), line.split("=")[1].trim());
				}
			}
		}

		if (settings.containsKey("Project"))
			projectName = settings.get("Project");
		if (settings.containsKey("Version"))
			versionName = settings.get("Version");

		if (settings.containsKey("task"))
			task = settings.get("task");
		if (settings.containsKey("repoDir"))
			repoDir = settings.get("repoDir");
		if (settings.containsKey("workingLoc"))
			workingLoc = settings.get("workingLoc");
		if (settings.containsKey("bugReport"))
			bugReport = settings.get("bugReport");
		if (settings.containsKey("changeOracle"))
			changeOracle = settings.get("changeOracle");
		if (settings.containsKey("lambda"))
			lambda = Double.parseDouble(settings.get("lambda"));
		if (settings.containsKey("belta1"))
			belta1 = Double.parseDouble(settings.get("belta1"));
		if (settings.containsKey("belta2"))
			belta2 = Double.parseDouble(settings.get("belta2"));
		if (settings.containsKey("sourceDir"))
			sourceDir = settings.get("sourceDir");

		//setting change
		String outputFile = workingLoc + File.separator + "Locus_" + projectName + "_" + versionName + "_output.txt";
		workingLoc = workingLoc + "Locus_" + projectName + "_" + versionName + "/";
		settings.put("workingLoc", workingLoc);
		settings.put("outputFile", outputFile);

		if (task.equals("") || repoDir.equals("") || workingLoc.equals("")
				|| bugReport.equals("") || changeOracle.equals("") || projectName.equals("") || versionName.equals("")) {
			System.out.println("Missing Required Configuration");
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		//boolean flag = false;
//		if (args.length > 0) {
//			flag = loadConfigure(args[0]);
//		} else {
//			System.out.println("Using default configuration file");
//			flag = loadConfigure("./config.txt");
//		}
		parseArguments(args);

		if (task.isEmpty() || repoDir.isEmpty() || workingLoc.isEmpty() || bugReport.isEmpty() || sourceDir.isEmpty()) {
			System.err.println("Missing required arguments. Ensure task, repoDir, workingLoc, bugReport and sourceDir are provided.");
			return;
		}

//		if (flag == true){
		System.out.println("working with " + projectName + " / " + versionName +" at location" + " " + workingLoc);
		startTask();
//		}
//		else
//			System.err.println("Error!, stop program..");

	}
}
