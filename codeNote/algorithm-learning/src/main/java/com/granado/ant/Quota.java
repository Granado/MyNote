package com.granado.ant;

import java.util.Comparator;

public class Quota implements Comparable<Quota> {

  private String id;

  private String groupId;

  private Float quota;

  public Quota() {

  }

  public Quota(String id, String groupId, float quota) {
    this.id = id;
    this.groupId = groupId;
    this.quota = quota;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(final String groupId) {
    this.groupId = groupId;
  }

  public float getQuota() {
    return quota;
  }

  public void setQuota(final float quota) {
    this.quota = quota;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public int compareTo(final Quota o) {
    int c = groupId.compareTo(o.groupId);
    return c == 0 ? quota.compareTo(o.quota) : c;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }

    if (obj instanceof Quota) {
      if (groupId.equals(((Quota) obj).groupId) && quota == ((Quota) obj).quota) {
        return true;
      }
    }

    return false;
  }
}
