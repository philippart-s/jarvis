package fr.wilda.picocli.sdk;

import java.io.Serializable;

public class OVHcloudUser implements Serializable{

  private String firstname;
  private String name;
  private String city;
  private String country;
  private String language;

  public String getFirstname() {
    return firstname;
  }
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getCity() {
    return city;
  }
  public void setCity(String city) {
    this.city = city;
  }
  public String getCountry() {
    return country;
  }
  public void setCountry(String country) {
    this.country = country;
  }
  public String getLanguage() {
    return language;
  }
  public void setLanguage(String language) {
    this.language = language;
  }

  public String toString() {
    return """
        First name: %s
        Last name: %s
        City: %s
        Country: %s
        Language: %s
        """.formatted(firstname, name, city, country, language);
  }
}