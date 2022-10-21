package com.utils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Properties;

public class ADdomain {
	 /**
     * AD域测试
     */
    private static int adTest() {
        System.out.println("start ");
        Properties env = new Properties();
        String adminName = "administrator@coolvisit.com";//username@domain
        String adminPassword = "As1234567";//password
        String ldapURL = "LDAP://172.16.109.112:389";//ip:port
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, adminName);
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);
        env.put(Context.PROVIDER_URL, ldapURL);

        try

        {
            LdapContext ctx = new InitialLdapContext(env, null);
            System.out.println("auth success ");
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchFilter = "(&(objectCategory=person)(objectClass=user)(name=*))";
            String searchBase = "DC=coolvisit,DC=com";
            String returnedAtts[] = {"memberOf","name","mobile","mail","department"};
            searchCtls.setReturningAttributes(returnedAtts);
            NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter, searchCtls);

            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                System.out.println("<<<::[" + sr.getName() + "]::>>>>");
                System.out.println("<<<::[" + sr.getAttributes().get("mail") + "]::>>>>");
                System.out.println("<<<::[" + sr.getAttributes() + "]::>>>>");
            }

            ctx.close();
        } catch ( NamingException e)

        {
            e.printStackTrace();
            System.err.println("Problem searching directory: " + e);
            return -2;
        }

        checkPw("lisi","As123456");
        System.err.println("end ");
        return 0;
    }

    public static int checkPw(String account,String pw){
        Properties env = new Properties();
        String adminName = account;//username@domain
        String adminPassword = pw;//password
        String ldapURL = "LDAP://172.16.109.112:389";//ip:port
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, adminName);
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);
        env.put(Context.PROVIDER_URL, ldapURL);

        try

        {
            LdapContext ctx = new InitialLdapContext(env, null);
            System.out.println(account+" auth success ");
            ctx.close();
        } catch ( NamingException e)
        {
            System.err.println("Problem searching directory: " + e);
            return -2;
        }
        return 0;
    }

}
