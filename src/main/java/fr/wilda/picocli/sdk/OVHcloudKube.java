package fr.wilda.picocli.sdk;

import java.io.Serializable;

public class OVHcloudKube implements Serializable{
  
    private String id;
    private String isUpToDate;
    private String name;
    private String region;
    private String status;
    private String updatePolicy;
    private String updatedAt;
    private String version;

    public String getId() {
      return id;
    }
    public void setId(String id) {
      this.id = id;
    }
    public String getIsUpToDate() {
      return isUpToDate;
    }
    public void setIsUpToDate(String isUpToDate) {
      this.isUpToDate = isUpToDate;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getRegion() {
      return region;
    }
    public void setRegion(String region) {
      this.region = region;
    }
    public String getStatus() {
      return status;
    }
    public void setStatus(String status) {
      this.status = status;
    }
    public String getUpdatePolicy() {
      return updatePolicy;
    }
    public void setUpdatePolicy(String updatePolicy) {
      this.updatePolicy = updatePolicy;
    }
    public String getUpdatedAt() {
      return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
      this.updatedAt = updatedAt;
    }
    public String getVersion() {
      return version;
    }
    public void setVersion(String version) {
      this.version = version;
    }
    @Override
    public String toString() {
      return """
          id: %s
          name: %s
          isUpTDate: %s
          region: %s
          status: %s
          updatePolicy: %s
          updatedAt: %s
          version: %s
          """.formatted(id, name, isUpToDate, region, status, updatePolicy, updatedAt, version);
    }


}
