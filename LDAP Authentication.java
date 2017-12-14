/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 *
 * @author Monish Verma
 */
public class DomainAuthentication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String username = "";
        String password = "";


        String url = "ldap://<ldap server>:port";
        String[] attrIdsToSearch = new String[]{"memberOf", "givenName", "SN"};
        String SEARCH_BY_SAM_ACCOUNT_NAME = "(sAMAccountName=%s)";
        String userBase = "DC=,DC=";

        Hashtable<String, String> environment = new Hashtable<>();

        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, url);
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, username + "@emailprovidor.com");
        environment.put(Context.SECURITY_CREDENTIALS, password);
        try {
            InitialDirContext context = new InitialDirContext(environment);

            String filter = String.format(SEARCH_BY_SAM_ACCOUNT_NAME, username);
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            constraints.setReturningAttributes(attrIdsToSearch);
            NamingEnumeration results = context.search(userBase, filter, constraints);

            // Fail if no entries found
            if (results == null || !results.hasMore()) {
                System.out.println("No result found");
                return;
            }

            // Get result for the first entry found
            SearchResult result = (SearchResult) results.next();
            //parse through string and retrieve first and last name
            String resultStr = result.toString();
            String firstName = resultStr.substring(resultStr.indexOf("Name: ") + 6, resultStr.indexOf(", m"));
            String lastName = resultStr.substring(resultStr.indexOf("sn=sn: ") + 7, resultStr.indexOf("}"));
            String fullName = firstName + " " + lastName;
            System.out.println("full name: "+fullName);
    }
        catch(NamingException e){
            System.out.println(e.getMessage());
        }

    }

}
