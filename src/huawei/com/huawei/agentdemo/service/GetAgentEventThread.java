package com.huawei.agentdemo.service;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.agentdemo.common.GlobalConstant;
import com.huawei.agentdemo.common.GlobalObjects;
import com.huawei.sdk.request.Request;
import com.huawei.sdk.util.StringUtils;

/**
 * <p>Title: obtain all online agent event  </p>
 * <p>Description: obtain all online agent event </p>
 */
public class GetAgentEventThread implements Runnable 
{

	private static Logger log = LoggerFactory.getLogger(GetAgentEventThread.class);
    
    private boolean isAlive = true;
    
    private String workNo;    
    
    /**
	 * @param workNo
	 */
	public GetAgentEventThread(String workNo) {
		super();
		this.workNo = workNo;
	}


	@SuppressWarnings("unchecked")
    @Override
    public void run()
    {        
        Map<String, Object> map = null;
        String retcode = null;
        Map<String, Object> event = null;
        String url = AgentService.prefix+"agentevent/"+workNo;
        while (isAlive)
        {        	
            try
            {
                map = null;
                map = Request.get(workNo,url);
//                System.out.println(StringUtils.beanToJson(map));
            }
            catch (Exception e)
            {
                log.debug("get event : "+e.getMessage());
            }
            
            if (map != null)
            {
                retcode = (String)map.get("retcode");
                if (GlobalConstant.BACK_TYPE_SUCCESS.equals(retcode))
                {
                	event = (Map<String, Object>) map.get("event");
                	if (event != null)
                    {                 		
                        //get logged in agent event only
                        if (GlobalObjects.loginedMap.containsKey(event.get("workNo")))
                        {
                            weccEventHandle(event);
                        }

                    }
                }else if("100-006".equals(retcode))    //agent not login
                {
                	try {
						GlobalObjects.loginedMap.get(workNo).putMessage(StringUtils.beanToJson(map));
						AgentService.clearResourse(workNo);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
            else 
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    log.debug(e.getMessage());
                }   
            }
        } 
    }
    
    
    /**
     * event handle method
     * @param event
     */
    public void weccEventHandle(Map<String, Object> event)
    {
        String workNo = (String) event.get("workNo");
        try
        {
            String message = StringUtils.beanToJson(event);
            GlobalObjects.loginedMap.get(workNo).putMessage(message);
        }
        catch (IOException e)
        {
            log.error("put event message into queue failed --" + e.getMessage());
        }
        
    }


    
    
    
//    /**
//     * start
//     * @throws RestClientException
//     */
//    public void begin() throws RestClientException
//    {        
//        isAlive = true;
//        this.start();
//    }
//    
    /**
     * stop
     */
    public void end()
    {
        isAlive = false;
    }
 
}
    