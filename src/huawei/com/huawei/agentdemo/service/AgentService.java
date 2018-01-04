
package com.huawei.agentdemo.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.agentdemo.bean.ProcessMessageQueue;
import com.huawei.agentdemo.common.GlobalObjects;
import com.huawei.agentdemo.util.ConfigList;
import com.huawei.agentdemo.util.ConfigProperties;
import com.huawei.sdk.request.Request;
import com.huawei.sdk.util.StringUtils;

/**
 * <p>Title: Agent Service </p>
 * <p>Description: Agent Service </p>
 */
public class AgentService
{
    
    /**
     * log
     */
    private static Logger log = LoggerFactory.getLogger(AgentService.class);

    public  static String ip = "";
    
    public  static int port = 0;
    
    public  static String localIP = "";
    
    public  static String prefix;
    
    public static ExecutorService threads = Executors.newCachedThreadPool();
    
        
    public static void init()
    {
        initConfig();
        prefix = "https://"+ip+":"+port+"/agentgateway/resource/";
    }    
    
    /**
     * agent login method
     * request method : PUT
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/onlineagent/{agentid}
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"password":"","phonenum":"40038","status":"4","releasephone":"false","agenttype":"4"}
     */
    public static String login(String workNo, String password, String phonenum,boolean isAutoanswer)
    {
        log.info(workNo + " : login begin.");
        
        String url = prefix+"onlineagent/"+workNo;      //请求路径
        
        
        String resp = "";
        
        Map<String, Object> loginParam = new HashMap<String, Object>();        
        loginParam.put("password",password);            // Indicates the password of an agent.
        loginParam.put("phonenum",phonenum);            // Indicates the phone number of an agent.
        loginParam.put("autoanswer",isAutoanswer);      // Indicates whether automatic answering is enabled. The default value is true.
        loginParam.put("autoenteridle",false);          // Indicates whether to enter the idle state automatically. The default value is true.
                                                        // if the value is false,the agent enter work state after login.
        loginParam.put("status",4);                     // Indicates the status of an agent right after the agent is logged in.
                                                        // The default status is idle.4:idle 5:wrap-up
        loginParam.put("releasephone", true);           // Indicates whether an agent can receive a new call without hanging up when the last call 
                                                        // is released. The default value is false.
        loginParam.put("ip",localIP);                   // Indicates the IP address of an agent. The default ip is 127.0.01.
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0","result":{"vdnid":1,"workno":"291","mediatype":"TTF","loginTime":"1471585949806",
         * "isForceChange":"false"}}
         */
        Map<String, Object> result = Request.put(workNo,url, loginParam);
        
        try
        {
        	String sendmsg = StringUtils.beanToJson(loginParam);
        	log.debug(workNo+" request URL : "+url);
        	log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e) 
        {
            log.warn("result is not variable" + e.getMessage());
        }
        if ("0".equals(result.get("retcode")))
        {
        	GlobalObjects.loginedMap.put(workNo, new ProcessMessageQueue());   // create a event queue for every logged in agent
        	GetAgentEventThread thread = new GetAgentEventThread(workNo);      // create a get event thread for every logged in agent
			threads.submit(thread);
			GlobalObjects.eventThreadMap.put(workNo, thread);
			
            log.info(workNo + " login success");
        }
        else 
        {
            log.info(workNo + " login failed ---" + resp);
        }        
        log.info(workNo + " : login end.");
        return resp;
    }
    
    /**
     * agent force login method
     * request method : PUT
     * 
     * request URL：http(s)://ip:port/agentgateway/resource/onlineagent/{agentid}/forcelogin
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"password":"","phonenum":"40038","status":"4","releasephone":"false","agenttype":"4"}
     */
    public static String forceLogin(String workNo, String password, String phonenum,boolean isAutoanswer)
    {
        log.info(workNo + " : forcelogin begin.");
        
        String url = prefix+"onlineagent/"+workNo+"/forcelogin";
        String resp = "";
        
        Map<String, Object> loginParam = new HashMap<String, Object>();       
        loginParam.put("password",password);            // Indicates the password of an agent.
        loginParam.put("phonenum",phonenum);            // Indicates the phone number of an agent.
        loginParam.put("autoanswer",isAutoanswer);      // Indicates whether automatic answering is enabled. The default value is true.
        loginParam.put("autoenteridle",false);          // Indicates whether to enter the idle state automatically. The default value is true.
                                                        // if the value is false,the agent enter work state after login.
        loginParam.put("status",4);                     // Indicates the status of an agent right after the agent is logged in.
                                                        // The default status is idle.4:idle 5:wrap-up
        loginParam.put("releasephone", true);           // Indicates whether an agent can receive a new call without hanging up when the last call 
                                                        // is released. The default value is false.
        loginParam.put("ip",localIP);                   // Indicates the IP address of an agent. The default ip is 127.0.01.
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0","result":{"vdnid":1,"workno":"291","mediatype":"TTF","loginTime":"1471585949806",
         * "isForceChange":"false"}}
         */
        Map<String, Object> result = Request.put(workNo,url, loginParam);
        try
        {
        	String sendmsg = StringUtils.beanToJson(loginParam);
        	log.debug(workNo+" request URL : "+url);
        	log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e) 
        {
            log.warn("result is not variable" + e.getMessage());
        }
        if ("0".equals(result.get("retcode")))
        {
        	GlobalObjects.loginedMap.put(workNo, new ProcessMessageQueue());   // create a event queue for every logged in agent
        	GetAgentEventThread thread = new GetAgentEventThread(workNo);      // create a get event thread for every logged in agent
			threads.submit(thread);
			GlobalObjects.eventThreadMap.put(workNo, thread);
            log.info(workNo + " forcelogin success");
        }
        else 
        {
            log.info(workNo + " login failed ---" + resp);
            GlobalObjects.loginedMap.remove(workNo);
        }
        log.info(workNo + " : forcelogin end.");
        return resp;
    }

    /**
     * agent logout method
     * request method : DELETE
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/onlineagent/{agentid}/logout
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String logout(String workNo)
    {
        log.info(workNo + " : logout begin.");
        
        String url = prefix+"onlineagent/"+workNo+"/logout";
        String resp = "";
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String, Object> result = Request.delete(workNo,url,null);
        
        try
        {        	
        	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e) 
        {
            log.warn("result is not variable" + e.getMessage());
        }
        if ("0".equals(result.get("retcode")))
        {
        	clearResourse(workNo);
            log.info(workNo + " logout success");
        }
        else 
        {
            log.info(workNo + " logout failed ---" + resp);
        }
        log.info(workNo + " : logout end.");
        return resp;
    }
    
    /**
     * agent set ready method 
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/onlineagent/{agentid}/sayfree
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String ready(String workNo)
    {
        log.info(workNo + " : set ready begin.");
        
        String url = prefix+"onlineagent/"+workNo+"/sayfree";
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String, Object> result = Request.post(workNo,url, null);
        
        try
        {
        	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e) 
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : set ready end.");
        return resp;
    }
    
    /**
     * agent set busy method
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/onlineagent/{agentid}/saybusy?reason={reason}
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.{reason}indicates the busy cause code (the value ranges from 200 to 250. 
     * If the value is set to 0 or the no-transfer reason, the busy cause code is not set).
     */
    public static String busy(String workNo)
    {
        log.info(workNo + " : set busy begin.");
        
        String url = prefix+"onlineagent/"+workNo+"/saybusy";
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String, Object> result = Request.post(workNo,url, null);
        
        try
        {
        	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e) 
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : set busy end.");
        return resp;
    }
    
    /**
     * agent enter working state method
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/onlineagent/{agentid}/work
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String work(String workNo)
    {
        log.info(workNo + " : enter work begin.");
        
        String url = prefix+"onlineagent/"+workNo+"/work";
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String, Object> result = Request.post(workNo,url, null);
        
        try
        {
        	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e) 
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : enter work end.");
        return resp;
    }
    
    /**
     * agent quit working state method
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/onlineagent/{agentid}/cancelwork
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String outwork(String workNo)
    {
        log.info(workNo + " : cancle work begin.");
        
        String url = prefix+"onlineagent/"+workNo+"/cancelwork";
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String, Object> result = Request.post(workNo,url, null);
        
        try
        {
        	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e) 
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : cancle work end.");
        return resp;
    }
    
    /**
     * agent reset skill method
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/onlineagent/{agentid}/resetskill/{autoflag}?skillid={skillid}&phonelinkage={phonelinkage}
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * {agentid} indicates the work ID of an agent, {autoflag} indicates the skill sign-in mode (value true indicates automatic signing in to agent 
     * configured skills, value false indicates manual signing in to agent configured skills, and other values are regarded as false), 
     * {skillid} indicates the skill queue ID (when sign-in mode is set to false, skillid is not a mandatory string-group parameter, for example, 1;2;3, 
     * and the agent signed-in skill queue is the intersection of skillid and configured skills and the maximum length is 100), 
     * and {phonelinkage} indicates whether phone linkage is enabled (the value 1 indicates that phone linkage is enabled and the value 0 indicates 
     * that phone linkage is disabled).
     */
    public static String resetSkills(String workNo)
    {
        log.info(workNo + " : reset skill begin.");
        
        String url = prefix+"onlineagent/"+workNo+"/resetskill/true";
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String, Object> result = Request.post(workNo,url, null);
        
        try
        {
        	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e) 
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : reset skill end.");
        return resp;
    }
    
    /**
     * agent answer voice call method
     * request method : PUT
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/answer
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String voiceAnswer(String workNo)
    {
        log.info(workNo + " : voicecall answer begin.");
        
        String url = prefix+"voicecall/"+workNo+"/answer";
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String, Object> result = Request.put(workNo,url, null);
        
        try
        {
        	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e) 
        {
            log.warn("result is not variable" + e.getMessage());
        }
        
        if ("0".equals(result.get("retcode")))
        {
            log.info(workNo + " call success");
        }
        else 
        {
            log.info(workNo + " call failed ---" + resp);
        }
        log.info(workNo + " : voicecall answer end.");
        return resp;
    }
    
    /**
     * agent release voice call method
     * request method : DELETE
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/release
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String voiceRelease(String workNo)
    {
        log.info(workNo + " : release voicecall begin.");
        
        String url = prefix+"voicecall/"+workNo+"/release";
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String,Object> result = Request.delete(workNo,url,null);
        try
        {
        	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        if ("0".equals(result.get("retcode")))
        {
            log.info(workNo + " release success");
        }
        else 
        {
            log.info(workNo + " release failed ---" + resp);
        }
        log.info(workNo + " : release voicecall end.");
        return resp;
    }
    
    /**
     * agent hold voice call method
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/hold
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String hold(String workNo)
    {
        log.info(workNo + " : hold voiceCall begin.");
        
        String url = prefix+"voicecall/"+workNo+"/hold";
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String,Object> result = Request.post(workNo,url, null);
        
        try
        {
        	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : hold voicecall end.");
        return resp;
    }
    
    /**
     * agent get hold list method
     * request method : GET
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/calldata/{agentid}/holdlist
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String getHoldList(String workNo)
    {
        log.info(workNo + " : get hold list begin.");
        
        String url = prefix+"calldata/"+workNo+"/holdlist";
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String,Object> result = Request.get(workNo,url);
        
        try
        {
            log.debug(workNo+" request URL : "+url);            
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : get hold list end.");
        return resp;
    }
    
    /**
     * agent cancel hold voice call method
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/gethold?callid={callid}
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway,
     * {agentid} indicates the work ID of an agent, and {callid} indicates the unique call ID.
     */
    public static String getHold(String workNo,String callId)
    { 
	    log.info(workNo + " : gethold voiceCall begin.");
	    
	    String url = prefix+"voicecall/"+workNo+"/gethold?callid="+callId;
	    
	    String resp = "";
	    
	    /*
	     * The following provides an example of the response message body of this interface:
	     * {"message":"","retcode":"0"}
	     */
	    Map<String,Object> result = Request.post(workNo,url, null);
	    try
	    {
	    	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
	    }
	    catch (IOException e)
	    {
	        log.warn("result is not variable" + e.getMessage());
	    }
	    log.info(workNo + " : get hold voicecall end.");
	    return resp;
    }
    
    /**
     * agent mute an ongoing call
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/beginmute
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String mute(String workNo)
    {
    	log.info(workNo + " : mute voiceCall begin.");
    	
 	    String url = prefix+"voicecall/"+workNo+"/beginmute";
 	    
 	    String resp = "";
 	    
 	   /*
	     * The following provides an example of the response message body of this interface:
	     * {"message":"","retcode":"0"}
	     */
 	    Map<String,Object> result = Request.post(workNo,url, null);
 	    try
 	    {
 	    	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
 	    }
 	    catch (IOException e)
 	    {
 	        log.warn("result is not variable" + e.getMessage());
 	    }
 	    log.info(workNo + " : mute voicecall end.");
 	    return resp;
    }
    
    /**
     * agent cancel mute a muted call
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/endmute
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     */
    public static String cancleMute(String workNo)
    {
    	log.info(workNo + " : cancle mute voiceCall begin.");
    	
 	    String url = prefix+"voicecall/"+workNo+"/endmute";
 	    
 	    String resp = "";
 	    
 	   /*
 	     * The following provides an example of the response message body of this interface:
 	     * {"message":"","retcode":"0"}
 	     */
 	    Map<String,Object> result = Request.post(workNo,url, null);
 	    try
 	    {
 	    	log.debug(workNo+" request URL : "+url);        	
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
 	    }
 	    catch (IOException e)
 	    {
 	        log.warn("result is not variable" + e.getMessage());
 	    }
 	    log.info(workNo + " : cancle mute voicecall end.");
 	    return resp;
    }
    
    /**
     * agent make an internal voice call method
     * request method : PUT
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/callinner
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway,
     * and {agentid} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"caller":"40038","called":"40040","skillid":25,"callappdata":"","callcontrolid":0,"mediaability":1}
     */
    public static String voicecallInner(String workNo,String called)
    {
    	log.info(workNo + " : voicecall inner begin.");
    	
 	    String url = prefix+"voicecall/"+workNo+"/callinner";
 	    
 	    Map<String, Object> callInnerParam = new HashMap<String, Object>();
 	    callInnerParam.put("called", called);  //called ID
 	    String resp = "";
 	    
 	   /*
 	     * The following provides an example of the response message body of this interface:
 	     * {"message":"","retcode":"0","result":"1455885056-1095"}
 	     */
 	    Map<String,Object> result = Request.put(workNo,url, callInnerParam);
 	    try
 	    {
 	    	String sendmsg = StringUtils.beanToJson(callInnerParam);
        	log.debug(workNo+" request URL : "+url);
        	log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
 	    }
 	    catch (IOException e)
 	    {
 	        log.warn("result is not variable" + e.getMessage());
 	    }
 	    log.info(workNo + " : voicecall inner end.");
 	    return resp;    	
    }
    
    /**
     * agent make a three-party call based on an ongoing call and a held call
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/confjoin
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"callid":"1455885056-1095", "callappdata":""}
     */
    public static String voiceCallThreePart(String workNo,String callid)
    {
        log.info(workNo + " : three part voiceCall begin.");
        
        String url = prefix+"voicecall/"+workNo+"/confjoin";
        
        Map<String, Object> threePartCallParam = new HashMap<String, Object>();
        threePartCallParam.put("callid", callid);// called NUM
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0","result":"1455885056-1095"}
         */
        Map<String,Object> result = Request.post(workNo,url, threePartCallParam);
        try
        {
            String sendmsg = StringUtils.beanToJson(threePartCallParam);
            log.debug(workNo+" request URL : "+url);
            log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : three part voicecall end.");
        return resp;
    }
    
    /**
     * agent ask for inner help
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/innerhelp
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"dstaddress":49997, "devicetype":2, "mode":1, "callappdata":""}
     */
    public static String voiceInnerHelp(String workNo,String dstaddress,int devicetype,int mode)
    {
        log.info(workNo + " : inner help begin.");
        
        String url = prefix+"voicecall/"+workNo+"/innerhelp";
        
        Map<String, Object> innerHelpParam = new HashMap<String, Object>();
        innerHelpParam.put("dstaddress", dstaddress);
        innerHelpParam.put("devicetype", devicetype);
        innerHelpParam.put("mode", mode);
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0","result":"1455885056-1095"}
         */
        Map<String,Object> result = Request.post(workNo,url, innerHelpParam);
        try
        {
            String sendmsg = StringUtils.beanToJson(innerHelpParam);
            log.debug(workNo+" request URL : "+url);
            log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : inner help end.");
        return resp;
    }
    
    /**
     * agent make an outgoing voice call method
     * request method : PUT
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/callout
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"caller":"40038","called":"40040","skillid":25,"callappdata":"","callcontrolid":0,"mediaability":1}
     */
    public static String voicecallOut(String workNo,String called)
    {
        log.info(workNo + " : callout voiceCall begin.");
        
        String url = prefix+"voicecall/"+workNo+"/callout";
        
        Map<String, Object> callOutParam = new HashMap<String, Object>();
        callOutParam.put("called", called);// called NUM
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0","result":"1455885056-1095"}
         */
        Map<String,Object> result = Request.put(workNo,url, callOutParam);
        try
        {
        	String sendmsg = StringUtils.beanToJson(callOutParam);
        	log.debug(workNo+" request URL : "+url);
        	log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : callout voicecall end.");
        return resp;
    }
    
    /**
     * agent transfer a voice call to another agent method
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/transfer
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"devicetype":2,"mode":3,"address":"49998","callappdata":"","caller":"40040","mediaability":0}
     */
    public static String voiceTranstoagent(String workNo , String address)
    {
    	log.info(workNo + " : transfer voiceCall to agent begin.");
    	
        String url = prefix+"voicecall/"+workNo+"/transfer";
        
        Map<String, Object> transferParam = new HashMap<String, Object>();
        transferParam.put("devicetype", 2);
        transferParam.put("mode", 3);
        transferParam.put("address", address);
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String,Object> result = Request.post(workNo,url, transferParam);
        try
        {
        	String sendmsg = StringUtils.beanToJson(transferParam);
        	log.debug(workNo+" request URL : "+url);
        	log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : transfer voicecall to agent end.");
        return resp;
    }   
    
    /**
     * agent transfer a voice call to a skill method
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/voicecall/{agentid}/transfer
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {agentid} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"devicetype":2,"mode":3,"address":"49998","callappdata":"","caller":"40040","mediaability":0}
     */
    public static String voiceTranstoskills(String workNo , String address)
    {
    	log.info(workNo + " : transfer voiceCall to skill begin.");
    	
        String url = prefix+"voicecall/"+workNo+"/transfer";
        
        Map<String, Object> transferParam = new HashMap<String, Object>();
        transferParam.put("devicetype", 1);
        transferParam.put("mode", 2);
        transferParam.put("address", address);
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String,Object> result = Request.post(workNo,url, transferParam);
        try
        {
        	String sendmsg = StringUtils.beanToJson(transferParam);
        	log.debug(workNo+" request URL : "+url);
        	log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : transfer voicecall to skill end.");
        return resp;
    }    
    
    /**
     * agent answer a text chat method
     * request method : PUT
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/textchat/{workno}/answer/{callid}
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * {workno} indicates the work ID of an agent, and {callid} indicates the ID of the text chat session.
     */
    public static String textAnswer(String workNo,String callId)
    {
    	log.info(workNo+" : answer textChat begin.");
    	
    	String url = prefix+"textchat/"+workNo+"/answer/"+callId;
    	
    	String resp = "";
    	
    	/*
    	 * The following provides an example of the response message body of this interface:
    	 * {"message":"","retcode":"0","result":"1455961792-16777554"}
    	 */
    	Map<String,Object> result = Request.put(workNo,url, null);
    	try
        {
        	log.debug(workNo+" request URL : "+url);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : answer textChat end.");
        return resp;
    }
    
    /**
     * agent terminate a text chat method
     * request method : DELETE
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/textchat/{workno}/drop/{callid}
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * {workno} indicates the work ID of an agent, and {callid} indicates the ID of the text chat session.
     */
    public static String textDrop(String workNo,String callId)
    {
    	log.info(workNo+" : drop textChat begin.");
    	
    	String url = prefix+"textchat/"+workNo+"/drop/"+callId;
    	
    	String resp = "";
    	
    	/*
    	 * The following provides an example of the response message body of this interface:
    	 * {"message":"","retcode":"0","result":"1455961792-16777554"}
    	 */
    	Map<String,Object> result = Request.delete(workNo,url, null);
    	try
        {
    		log.debug(workNo+" request URL : "+url);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : drop textChat end.");
        return resp;
    }
    
    /**
     * agent send a text message method
     * request method : PUT
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/textchat/{workno}/chatmessage
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {workno} indicates the work ID of an agent.
     */
    public static String textSendmessage(String workNo,String callId,String content)
    {
    	log.info(workNo+" : textChat send message begin.");
    	
    	String url = prefix+"textchat/"+workNo+"/chatmessage/";
    	
    	Map<String, Object> chatmessage = new HashMap<String, Object>();
    	chatmessage.put("callId", callId);
    	chatmessage.put("content", content);
    	
    	String resp = "";
    	
    	/*
    	 * The following provides an example of the response message body of this interface:
    	 * {"message":"","retcode":"0","result":"1455961792-16777554"}
    	 */
    	Map<String,Object> result = Request.put(workNo,url, chatmessage);
    	try
        {
    		String sendmsg = StringUtils.beanToJson(chatmessage);
        	log.debug(workNo+" request URL : "+url);
        	log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : textChat send message end.");
        return resp;
    }
    
    /**
     * agent request an internal text chat method
     * request method : PUT
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/textchat/{workno}/internalcall/{destno}
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway,
     * {workno} indicates the work ID of an agent, and {destno} indicates the called work ID. 
     */
    public static String textCallinner(String workNo,String called)
    {
    	log.info(workNo+" : inner textChat begin.");
    	
    	String url = prefix+"textchat/"+workNo+"/internalcall/"+called;
    	
    	String resp = "";
    	
    	/*
    	 * The following provides an example of the response message body of this interface:
    	 * {"message":"","retcode":"0","result":"49999"}
    	 */
    	Map<String,Object> result = Request.put(workNo,url, null);
    	try
        {
    		log.debug(workNo+" request URL : "+url);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : inner textChat end.");
        return resp;
    }
    
    /**
     * agent transfer a text chat to another agent
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/textchat/{workno}/transfer/
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {workno} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"addesstype":1,"destaddr":"291","callid":"1456108492-16777565","attachdata":""}
     */
    public static String textTranstoAgent(String workNo , String address, String callId)
    {
    	log.info(workNo + " : transfer textChat to agent begin.");
    	
        String url = prefix+"textchat/"+workNo+"/transfer";
        
        Map<String, Object> transferParam = new HashMap<String, Object>();
        transferParam.put("addesstype", 1);
        transferParam.put("destaddr", address);
        transferParam.put("callid", callId);
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String,Object> result = Request.post(workNo,url, transferParam);
        try
        {
        	String sendmsg = StringUtils.beanToJson(transferParam);
        	log.debug(workNo+" request URL : "+url);
        	log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : transfer textChat to agent end.");
        return resp;
    } 
    
    /**
     * agent transfer a text chat to skill
     * request method : POST
     * 
     * request URL : http(s)://ip:port/agentgateway/resource/textchat/{workno}/transfer/
     * In this URL, ip indicates the IP address of the Agent Gateway, port indicates the HTTP or HTTPS port number of the Agent Gateway, 
     * and {workno} indicates the work ID of an agent.
     * 
     * The following provides an example of the request message body of this interface:
     * {"addesstype":1,"destaddr":"291","callid":"1456108492-16777565","attachdata":""}
     */
    public static String textTranstoSkills(String workNo , String address, String callId)
    {
    	log.info(workNo + " : transfer textChat to skill begin.");
    	
        String url = prefix+"textchat/"+workNo+"/transfer";
        
        Map<String, Object> transferParam = new HashMap<String, Object>();
        transferParam.put("addesstype", 10);
        transferParam.put("destaddr", address);
        transferParam.put("callid", callId);
        
        String resp = "";
        
        /*
         * The following provides an example of the response message body of this interface:
         * {"message":"","retcode":"0"}
         */
        Map<String,Object> result = Request.post(workNo,url, transferParam);
        try
        {
        	String sendmsg = StringUtils.beanToJson(transferParam);
        	log.debug(workNo+" request URL : "+url);
        	log.debug(workNo+" request message : "+sendmsg);
            resp = StringUtils.beanToJson(result);
            log.debug(workNo+" receive message : "+resp);
        }
        catch (IOException e)
        {
            log.warn("result is not variable" + e.getMessage());
        }
        log.info(workNo + " : transfer textChat to skill end.");
        return resp;
    }   
    
    /**
     * configuration parameters initialize method
     */
    public static void initConfig()
    {
        try
        {
            ip = ConfigProperties.getKey(ConfigList.BASIC, "AgentGateway_IP");
            localIP = ConfigProperties.getKey(ConfigList.BASIC, "Local_IP");
            port = Integer.parseInt(ConfigProperties.getKey(ConfigList.BASIC, "AgentGateway_PORT"));
        }
        catch (NumberFormatException e)
        {
            log.error("read properties failed --", e);
        }
    }
    
    /**
     * resource clear
     */
    public static void clearResourse(String workNo)
    {
    	GlobalObjects.eventThreadMap.get(workNo).end();
    	GlobalObjects.eventThreadMap.remove(workNo);
    	GlobalObjects.guidMap.remove(workNo);
    	GlobalObjects.elbSessionMap.remove(workNo);//For HEC
//    	GlobalObjects.cookieMap.remove(workNo);
//    	GlobalObjects.loginedMap.remove(workNo);
    }
}
