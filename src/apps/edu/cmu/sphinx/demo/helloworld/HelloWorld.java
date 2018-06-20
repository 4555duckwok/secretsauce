/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.demo.helloworld;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import java.util.Scanner;
/**
 * A simple HelloWorld demo showing a simple speech application built using Sphinx-4. This application uses the Sphinx-4
 * endpointer, which automatically segments incoming audio into utterances and silences.
 */
public class HelloWorld {

    public static void main(String[] args) {
        ConfigurationManager cm;

        if (args.length > 0) {
            cm = new ConfigurationManager(args[0]);
        } else {
            cm = new ConfigurationManager(HelloWorld.class.getResource("helloworld.config.xml"));
        }

        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        recognizer.allocate();

        // start the microphone or exit if the programm if this is not possible
        Microphone microphone = (Microphone) cm.lookup("microphone");
        if (!microphone.startRecording()) {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
        }

        System.out.println("Say: (Initiate | Acknowledge | Resolve) ( Alert )");
        Scanner in = new Scanner(System.in);
        // loop the recognition until the programm exits.
        while (true) {
            System.out.println("Start speaking. Press Ctrl-C to quit.\n");
            
            Result result = recognizer.recognize();
            if(result.getBestFinalResultNoFiller().equals("and on")){
            	int a = 0;
            	while (true) {
            		a++;
            		System.out.println("eAndon Triggered " + result.getBestFinalResultNoFiller());
            		
            		Result result2 = recognizer.recognize();
                	
                	if (result2 != null) {
                        String resultText = result2.getBestFinalResultNoFiller();
                        System.out.println(resultText);
                        if(resultText.length() > 12 && (resultText.substring(0, 8).equals("initiate") || resultText.substring(0, 11).equals("acknowledge") ||resultText.substring(0, 7).equals("resolve"))){
                        	resultText = resultText.replace(" ", "");
                        	resultText = resultText.replace("zero", "0");
                        	resultText = resultText.replace("one", "1");
                        	resultText= resultText.replace("two", "2");
                        	resultText = resultText.replace("three", "3");
                        	resultText = resultText.replace("four", "4");
                        	resultText = resultText.replace("five", "5");
                        	resultText = resultText.replace("six", "6");
                        	resultText = resultText.replace("seven", "7");
                        	resultText = resultText.replace("eight", "8");
                        	resultText = resultText.replace("nine", "9");
                        	
                        	resultText = resultText.replace("resolve", "resolve ");
                        	resultText = resultText.replace("initiate", "initiate ");
                        	resultText = resultText.replace("acknowledge", "acknowledge ");
                        	if(resultText.substring(0, 8).equals("initiate")){
                        		int alertDefId = Integer.parseInt(resultText.substring(9));
                        		System.out.println(executePost("https://test-eandon-metadata.run.aws-usw02-pr.ice.predix.io/api/v1/alertDefinitions/"+alertDefId+"/alerts",
                        				"{\"comment\": \"This alert was created via voice activation\",\"sso\": \"212326570\"}", resultText.substring(9)));
                        		
                        	}
                        	else if(resultText.substring(0, 11).equals("acknowledge")){
                        		int alertDefId = Integer.parseInt(resultText.substring(12));
                        	}
                        	else if(resultText.substring(0, 7).equals("resolve")){
                        		int alertDefId = Integer.parseInt(resultText.substring(8));
                        	}
                        	System.out.println("You said: " + resultText + '\n' + resultText.substring(0, 8));
                        	break;
                        }
                        	
                    } else {
                        System.out.println("I can't hear what you said.\n");
                    }
                	if(a == 2)
                		break;
            	}
            	
            }
            
        }
    }
    public static String executePost(String targetURL, String urlParameters, String alertDefId) {
    	  HttpURLConnection connection = null;

    	  try {
    	    //Create connection
    	    URL url = new URL(targetURL);
    	    connection = (HttpURLConnection) url.openConnection();
    	    connection.setRequestMethod("POST");
    	    connection.setRequestProperty("Content-Type", 
    	        "application/json;charset=UTF-8");
    	    connection.setRequestProperty("alertDefinitionId", alertDefId);
    	    connection.setRequestProperty("authorization", 
    	        "bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiIzZTg2NWQyYTE1NzY0MGJlYjg5NDk5OTBiYjliN2VmZCIsInN1YiI6ImhlYWx0aGNhcmVfRmFjdG9yeV8zNjBfZGV2Iiwic2NvcGUiOlsidmlld3MuYWRtaW4udXNlciIsInVhYS5yZXNvdXJjZSIsInZpZXdzLnpvbmVzLjk4YWQxN2FmLWE2YmQtNDhkYy05NWRlLTMxMDQ0MGM0MmJjMC51c2VyIiwib3BlbmlkIiwidmlld3Muem9uZXMuZDc5ZmI4ZjMtOGY1MC00MTQ0LTg1MTQtOWRjMDZhYTYzZWNkLnVzZXIiLCJ2aWV3cy5wb3dlci51c2VyIiwidmlld3Muem9uZXMuNzNkMmI2ZTYtMGIzYS00MzczLWI4YTctNTE3MTk5ZDYwMGYzLnVzZXIiXSwiY2xpZW50X2lkIjoiaGVhbHRoY2FyZV9GYWN0b3J5XzM2MF9kZXYiLCJjaWQiOiJoZWFsdGhjYXJlX0ZhY3RvcnlfMzYwX2RldiIsImF6cCI6ImhlYWx0aGNhcmVfRmFjdG9yeV8zNjBfZGV2IiwiZ3JhbnRfdHlwZSI6ImNsaWVudF9jcmVkZW50aWFscyIsInJldl9zaWciOiJhZGVmNDIzMCIsImlhdCI6MTUyOTUwMjY3NiwiZXhwIjoxNTI5NTA5ODc1LCJpc3MiOiJodHRwczovL2E4YTJmZmM0LWIwNGUtNGVjMS1iZmVkLTdhNTFkZDQwODcyNS5wcmVkaXgtdWFhLnJ1bi5hd3MtdXN3MDItcHIuaWNlLnByZWRpeC5pby9vYXV0aC90b2tlbiIsInppZCI6ImE4YTJmZmM0LWIwNGUtNGVjMS1iZmVkLTdhNTFkZDQwODcyNSIsImF1ZCI6WyJ2aWV3cy56b25lcy5kNzlmYjhmMy04ZjUwLTQxNDQtODUxNC05ZGMwNmFhNjNlY2QiLCJ2aWV3cy56b25lcy45OGFkMTdhZi1hNmJkLTQ4ZGMtOTVkZS0zMTA0NDBjNDJiYzAiLCJ1YWEiLCJvcGVuaWQiLCJ2aWV3cy5hZG1pbiIsInZpZXdzLnBvd2VyIiwidmlld3Muem9uZXMuNzNkMmI2ZTYtMGIzYS00MzczLWI4YTctNTE3MTk5ZDYwMGYzIiwiaGVhbHRoY2FyZV9GYWN0b3J5XzM2MF9kZXYiXX0.sOVAfn8s3rCVMgrEYUCmVK01AWJvdErVpC0qxeRQ78K2QfoDyihnc7RonjxNfoJL_s1ZfhfgFYpbzxYCRDflPrtXZ6-xlksakW9mdSCFuwcf-QN1mkhfPiwmNf7BgU3vXJXF2uYTW8aAvKGB6gyYQk8rrs6Pd5qOhPhQKJQUSDSweu2l79udz7stwlPpHkKghgR-dnQwEn3f0SCSK9cnvOU8j2RjdBjrt7n6Kx3YRoM0Xc07CxBH-JsKuFv3jj3dVtlX68I4YjQaMyFGkDDDCi5Id5wy4D8ouNqH51_lwDZ9HVkHTgi48cVahkr5gxuWu8y50YBqN8rtH2lYHvGmKQ");
    	    connection.setRequestProperty("Content-Language", "en-US");  

    	    connection.setUseCaches(false);
    	    connection.setDoOutput(true);

    	    //Send request
    	    DataOutputStream wr = new DataOutputStream (
    	        connection.getOutputStream());
    	    wr.writeBytes(urlParameters);
    	    wr.close();

    	    //Get Response  
    	    InputStream is = connection.getInputStream();
    	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    	    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
    	    String line;
    	    while ((line = rd.readLine()) != null) {
    	      response.append(line);
    	      response.append('\r');
    	    }
    	    rd.close();
    	    return response.toString();
    	  } catch (Exception e) {
    	    e.printStackTrace();
    	    return null;
    	  } finally {
    	    if (connection != null) {
    	      connection.disconnect();
    	    }
    	  }
    	}
}
