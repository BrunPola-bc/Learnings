package week2.model;

public class Project {
  private int id;
  private String projectName;

  public Project() {}

  public Project(String projectName) {
    this.projectName = projectName;
  }

  public Project(int id, String projectName) {
    this.id = id;
    this.projectName = projectName;
  }

  public int getId() {
    return id;
  }

  // public void setId(int id) {
  //   this.id = id;
  // }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  @Override
  public String toString() {
    return "Project [id=" + id + ", projectName=" + projectName + "]";
  }
}
