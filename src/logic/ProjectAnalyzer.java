package logic;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Commit;
import entity.FixedBug;
import utlis.CSVWriter;
import utlis.JSONParser;

public class ProjectAnalyzer {

	private String projectName;
	private String gitProjectName;
	private static final String SEARCH_QUERY = "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt=";
	private static final String JIRA_HTTP_URL = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22";
	private String url;
	private List<FixedBug> fixedBugs;

	public ProjectAnalyzer(String projectName, String gitProjectName) {
		this.projectName = projectName;
		this.gitProjectName = gitProjectName;
		this.url = JIRA_HTTP_URL + this.projectName + SEARCH_QUERY;
	}

	public void analyze() throws JSONException, IOException, ParseException {
		fixedBugs = new ArrayList<>();

		Integer i = 0;
		Integer j = 0;
		Integer total = 1;
		do {
			j = i + 1000;
			String urlCombined = url + i.toString() + "&maxResults=" + j.toString();
			JSONObject json = JSONParser.readJsonFromUrl(urlCombined);

			JSONArray issues = json.getJSONArray("issues");
			total = json.getInt("total");

			for (; i < total && i < j; i++) {
				JSONObject issue = issues.getJSONObject(i % 1000);
				String key = issue.get("key").toString();
				String dateString = issue.getJSONObject("fields").get("resolutiondate").toString();
				StringTokenizer tokenizer = new StringTokenizer(dateString, "T");
				String date = tokenizer.nextToken();

				fixedBugs.add(new FixedBug(key, date));
			}

		} while (i < total);

	}
	
	//il mapping Jira-Git è stato implementato ma non è funzionante per il progetto Falcon, questo perchè su Github è diventato "read-only", non potendo recuperare i commit
	public void mapOnGit() throws IOException, GitAPIException {
		GitAPI git = new GitAPI(this.gitProjectName, "output/");
		git.init();

		List<Commit> commits = git.getCommits();
		for (Commit c : commits) {
			for (FixedBug b : fixedBugs) {
				if (c.getComment().contains(b.getKey()) && (b.getDate().compareTo(c.getDate()) < 0)) {
					b.setDate(c.getDate());
				}
			}
		}
	}

	public List<FixedBug> getFixedBugs() {
		return this.fixedBugs;
	}

	public static void main(String[] args) throws IOException, JSONException, ParseException {


		ProjectAnalyzer analyzer = new ProjectAnalyzer("FALCON", "falcon");
		analyzer.analyze();

		//analyzer.mapOnGit();
		//List<FixedBug> fixedBugs = analyzer.getFixedBugs();
		
		CSVWriter.write("FALCON", analyzer.getFixedBugs());
	}

}
