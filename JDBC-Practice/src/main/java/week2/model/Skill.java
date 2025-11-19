package week2.model;

public class Skill {
  private int id;
  private String skillName;

  public Skill() {}

  public Skill(String skillName) {
    this.skillName = skillName;
  }

  public Skill(int id, String skillName) {
    this.id = id;
    this.skillName = skillName;
  }

  public int getId() {
    return id;
  }

  // public void setId(int id) {
  //   this.id = id;
  // }

  public String getSkillName() {
    return skillName;
  }

  public void setSkillName(String skillName) {
    this.skillName = skillName;
  }

  @Override
  public String toString() {
    return "Skill [id=" + id + ", skillName=" + skillName + "]";
  }
}
