
package com.huawei.agentdemo.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.huawei.agentdemo.bean.ProcessMessageQueue;
import com.huawei.agentdemo.service.GetAgentEventThread;


public class GlobalObjects
{
    /**
     * Logged in agent 
     * key : agent ID
     * value : agent event queue 
     */
    public static Map<String, ProcessMessageQueue> loginedMap = new ConcurrentHashMap<String, ProcessMessageQueue>();
    
    /**
     * get agent event thread(for client-server mode)
     * key : agent ID
     * value : thread
     */
    public static Map<String, GetAgentEventThread> eventThreadMap = new ConcurrentHashMap<String, GetAgentEventThread>();
    
    /**
     * guid for authentication(for client-server mode)
     * key : agent ID
     * value : guid
     */
    public static Map<String,String> guidMap = new ConcurrentHashMap<String, String>();  
    
    /**
     * cookie
     * key : agent ID
     * value : sessionID
     */
    public static Map<String,String> cookieMap = new ConcurrentHashMap<String, String>();  
        
    /**
     * elb session(For HEC)
     * key : agent ID
     * value : huaweielbsession=***
     */
    public static Map<String, String> elbSessionMap = new ConcurrentHashMap<String, String>();
}
