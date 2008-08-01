package multimonster.common;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a criteria for which the db can be queried.
 * 
 * the attributes of this container depend mainly on the properties of MediaObject-metadata.
 * there are three classes of search-fields:
 * - open search (e.g. name)
 * - classified search (e.g. language)
 * - compareable search(e.g. time: after, before)
 * 
 * for classified search, every value that occures in database has to be transmitted outside.
 * 
 * if a value is empty, the search result won't be affected by this attribute 
 */
public class SearchCriteria  implements Serializable{
    private String title;
    private String actorName;

    /**
     * one of the entries of the languageDomain-Array 
     */
    private String language;

    /**
     * contains for each different value one entry in the array 
     */
    private String[] languageDomain;
    private String genre;
    private String[] genreDomain;
    private Date dateOfRelease; 
    //private Compare dateOfReleaseComp;
	/**
	 * @return
	 */
	public String getActorName() {
		return actorName;
	}

	/**
	 * @return
	 */
	public Date getDateOfRelease() {
		return dateOfRelease;
	}

	/**
	 * @return
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @return
	 */
	public String[] getGenreDomain() {
		return genreDomain;
	}

	/**
	 * @return
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @return
	 */
	public String[] getLanguageDomain() {
		return languageDomain;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param string
	 */
	public void setActorName(String string) {
		actorName = string;
	}

	/**
	 * @param date
	 */
	public void setDateOfRelease(Date date) {
		dateOfRelease = date;
	}

	/**
	 * @param string
	 */
	public void setGenre(String string) {
		genre = string;
	}

	/**
	 * @param strings
	 */
	public void setGenreDomain(String[] strings) {
		genreDomain = strings;
	}

	/**
	 * @param string
	 */
	public void setLanguage(String string) {
		language = string;
	}

	/**
	 * @param strings
	 */
	public void setLanguageDomain(String[] strings) {
		languageDomain = strings;
	}

	/**
	 * @param string
	 */
	public void setTitle(String string) {
		title = string;
	}

}
