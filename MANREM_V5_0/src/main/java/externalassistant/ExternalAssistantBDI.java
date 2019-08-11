/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package externalassistant;

import java.io.BufferedReader;
import java.io.IOException;

import services.chatService.ChatService;
import services.chatService.IChatService;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import webfileenergyprices.HttpDownloadUtility;
import webfileenergyprices.ReadWebFile;

/**
 *
 * @author Filipe Silvério
 */
@Agent
@Description("ExternalAssistant agent. <br>")
@Arguments
(value={
	@Argument(name="chatOn", description="externalAssistantBDI.chatOn", clazz=String.class, defaultvalue="\"0\""), 
})
@RequiredServices
({
	@RequiredService(name="clockservice", type=IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="chatservices", type=IChatService.class, multiple=true, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true))
})
@ProvidedServices
({
	@ProvidedService(type=IChatService.class, implementation=@Implementation(ChatService.class))
})
public class ExternalAssistantBDI{

	@AgentFeature 
	protected IBDIAgentFeature bdiFeature;
	
	@AgentArgument
	protected String chatOn;
	
    public static String[] date;
    public static double[] ptmarginalenergyprices = new double[24];
    public static double[] esmarginalenergyprices = new double[24];
    
    @AgentCreated
    protected void init() {
        System.out.println("Real Time Agent initiated!");
        
        bdiFeature.adoptPlan("ExternalAssistantPlan");
    }

    @Plan
    public void ExternalAssistantPlan()
    {
        String[] dateString;
        ReadWebFile r = new ReadWebFile();
        dateString = r.getDate();
        date = dateString;
        
        String fileURL = "http://www.omie.es/datosPub/marginalpdbcpt/marginalpdbcpt_"+dateString[0]+dateString[1]+dateString[2]+".1";
        //String saveDir = "C:\\Users\\Filipe\\Desktop\\LNEG";
        String saveDir = "webfiles\\Energy Prices";
        try {
            HttpDownloadUtility.downloadFile(fileURL, saveDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        String string = saveDir + "\\marginalpdbcpt_"+dateString[0]+dateString[1]+dateString[2]+".1";

        BufferedReader br_pt = r.openFile(string);
        BufferedReader br_es = r.openFile(string);
        this.ptmarginalenergyprices = r.readFile( br_pt, "pt");
        this.esmarginalenergyprices = r.readFile( br_es, "es");
        r.closeFile( br_pt );
        r.closeFile( br_es );
    }
    
    
}
