package logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import entity.Commit;

public class GitAPI {

	private static String gitUrl = "https://github.com/apache/";
	
	private String projectName;
	
	private Git git;
	
	private String repoDir;
	
	private String baseDir;
	
	private List<Commit> commits;
	
	
	public GitAPI(String projectName, String baseDir) {
		this.projectName = projectName.toLowerCase();
		if (!baseDir.substring(baseDir.length() - 1).contains("/")) {
			this.baseDir = baseDir + "/";
		} else {
			this.baseDir = baseDir;
		}
	}
	
	public void init() throws IOException, GitAPIException {
		this.repoDir = this.baseDir + projectName.toLowerCase() + "/" + projectName + "_repo";
		
		if (!Files.exists(Paths.get(repoDir))) {
			this.git = Git.cloneRepository()
					.setURI(gitUrl + projectName + ".git")
					.setDirectory(new File(repoDir))
					.call();
		} else {
			try (Git gitRepo = Git.open(new File( repoDir + "/.git"))){
				this.git = gitRepo;
				gitRepo.checkout().setName(this.getDefaultBranch()).call();
				gitRepo.pull().call();
				
			}
		}
	}
	
	private String getDefaultBranch() throws GitAPIException {
		List<Ref> branches = this.git.branchList().setListMode(ListMode.ALL).call();
		for (Ref branch: branches) {
			String branchName = branch.getName();
			if (branchName.startsWith("refs/heads/")) {
				return branchName.substring("refs/heads/".length());
			}
		}
		return "";
		
	}
	
	public String getRepoDir() {
		return repoDir;
	}

	public List<Commit> getCommits() throws GitAPIException {
		if (this.commits != null) {
			return this.commits;
		}
		
		this.commits = new ArrayList<>();  
        Iterable<RevCommit> commitsLog = null;
        
		this.git.checkout().setName(this.getDefaultBranch()).call();
		commitsLog = git.log().call();
       
        for (RevCommit commit : commitsLog) {
        	ObjectId parentId = (commit.getParentCount() != 0) ? commit.getParent(0).getId() : null;
        	this.commits.add(new Commit(commit.getId(), new Date(commit.getCommitTime() * 1000L), commit.getFullMessage(), parentId));
        }
                    
        return this.commits;
	}
	
}
	
