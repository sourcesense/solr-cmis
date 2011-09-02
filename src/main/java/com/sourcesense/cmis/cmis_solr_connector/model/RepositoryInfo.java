package com.sourcesense.cmis.cmis_solr_connector.model;

/**
 * This class models a Repository .
 * @author Alessandro Benedetti
 *
 */
public class RepositoryInfo {
  
  private String url;
  private String username;
  private String password;
  private String space;
  
  
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public RepositoryInfo(String url, String username, String password) {
    super();
    this.url = url;
    this.username = username;
    this.password = password;
   
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RepositoryInfo other = (RepositoryInfo) obj;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    if (url == null) {
      if (other.url != null)
        return false;
    } else if (!url.equals(other.url))
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    return true;
  }
  
  public String toString(){
    return "Url : "+this.url+"\n User :"+this.username+"\n Password : "+"********"+" Search space:"+this.space;
  }

  public void setSpace(String space) {
    this.space = space;
  }

  public String getSpace() {
    return space;
  }

  

}
