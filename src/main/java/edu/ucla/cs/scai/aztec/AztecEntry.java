package edu.ucla.cs.scai.aztec;

import edu.ucla.cs.scai.aztec.utils.ImageUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import com.google.gson.annotations.Expose;

import javax.print.DocFlavor;

/**
 * Class of AztecEntry
 *
 * @author Giuseppe M. Mazzeo "mazzeo@cs.ucla.edu"
 * @author Xinxin Huang "xinxinh@gmail.com"
 * @author Zeyu Li "zyli@cs.ucla.edu"
 */

public class AztecEntry {

    /**
     * General attributes of an AztecEntry
     */

    private String name;
    private String description;
    private transient String logo;
    private String source;
    private transient String language;
    private ArrayList<String> platforms;
    private transient String sourceCodeURL;
    private ArrayList<String> linkDescriptions;
    private ArrayList<String> linkUrls;
    private ArrayList<String> institutions;
    private ArrayList<String> maintainers;
    private ArrayList<String> maintainerEmails;
    private transient ArrayList<String> types;
    private ArrayList<String> tags;
    private ArrayList<String> domains;
    private transient Date[] dateCreated;
    private transient Date[] dateUpdated;
    private ArrayList<String> owners;
    private ArrayList<String> authors;
    private String id;
    private String version;
    private double[] logoHistogram;
    private transient ArrayList<String> suggestTag;
    private transient ArrayList<String> suggestTagPrefix;
    private transient String suggestName;
    private transient String suggestNamePrefix;
    private transient String acknowledgements;
    private transient String lastUpdatedMilliseconds;
    private transient String summary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ArrayList<String> getPlatforms() { return platforms; }

    public void setPlatforms(ArrayList<String> platforms) {
        this.platforms = platforms;
    }

    public String getSourceCodeURL() {
        return sourceCodeURL;
    }

    public void setSourceCodeURL(String sourceCodeURL) {
        this.sourceCodeURL = sourceCodeURL;
    }

    public Date[] getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date[] dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date[] getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date[] dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<String> getLinkDescriptions() {
        return linkDescriptions;
    }

    public void setLinkDescriptions(ArrayList<String> linkDescriptions) {
        this.linkDescriptions = linkDescriptions;
    }

    public ArrayList<String> getLinkUrls() {
        return linkUrls;
    }

    public void setLinkUrls(ArrayList<String> linkUrls) {
        this.linkUrls = linkUrls;
    }

    public ArrayList<String> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(ArrayList<String> institutions) {
        this.institutions = institutions;
    }

    public ArrayList<String> getMaintainers() {
        return maintainers;
    }

    public void setMaintainers(ArrayList<String> maintainers) {
        this.maintainers = maintainers;
    }

    public ArrayList<String> getMaintainerEmails() {
        return maintainerEmails;
    }

    public void setMaintainerEmails(ArrayList<String> maintainerEmails) {
        this.maintainerEmails = maintainerEmails;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getDomains() {
        return domains;
    }

    public void setDomains(ArrayList<String> domains) {
        this.domains = domains;
    }

    public ArrayList<String> getOwners() {
        return owners;
    }

    public void setOwners(ArrayList<String> owners) {
        this.owners = owners;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    private void computeLogoHistogram() throws Exception {
        logoHistogram = new ImageUtils().histogramFromImage(logo);
    }

    /**
     * Compute and return the Histogram of the Logo image
     *
     * @return the computed histogram
     */
    public double[] getLogoHistogram() {
        if (logoHistogram == null) {
            try {
                computeLogoHistogram();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return logoHistogram;
    }

    /**
     * Get the hashcode of an AztecEntry object.
     *
     * @return the hash code of an AztecEntry object
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.id);
        return hash;
    }

    /**
     * Compare two objects and return if they are identical
     *
     * @param obj the object to compare with.
     * @return <b>true</b> if identical. <b>false</b> if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AztecEntry other = (AztecEntry) obj;
        return Objects.equals(this.id, other.id);
    }

}
