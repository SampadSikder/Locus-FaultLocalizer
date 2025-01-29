package utils;

import generics.Bug;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import utils.FileToLines;
import utils.ReadFileToList;


public class ReadBugsFromXML {

	/**
	 * This method is used to read a list of Bugs from a file formated with XML
	 * @param loc
	 * @param pro
	 * @param version
	 * @return
	 */

	public static List<Bug> getFixedBugsFromXML(String bugFile) {
		List<Bug> fixedBugs = new ArrayList<>();
		try {
			String content = ReadFileToList.readFiles(bugFile); // Read XML content
			Document dom = DocumentHelper.parseText(content);
			Element rootElt = dom.getRootElement();
			Element innerRepo = rootElt.element("bugrepository"); // Access nested <bugrepository>
			List<Element> bugEle = innerRepo.elements("bug");

			for (Element bug : bugEle) {
				int bid = Integer.parseInt(bug.attributeValue("id"));
				long openDate = Long.parseLong(bug.attributeValue("opendate"));
				long fixDate = Long.parseLong(bug.attributeValue("fixdate"));

				Element bugInfo = bug.element("buginformation");
				String summary = bugInfo.elementText("summary");
				String description = bugInfo.elementText("description");
				Bug oneBug = new Bug(bid, summary, description, "Fixed", "", "", "", "", "", openDate, fixDate);

				List<Element> files = bug.element("fixedFiles").elements("file");
				for (Element file : files) {
					String fileName = file.getTextTrim();
					if (fileName.contains("/")) fileName = fileName.replace("/", ".");
					oneBug.addFile(fileName);
				}

				fixedBugs.add(oneBug);
			}
		} catch (Exception e) {
			e.printStackTrace(); // Debug output, consider logging in production
		}
		return fixedBugs;
	}

}