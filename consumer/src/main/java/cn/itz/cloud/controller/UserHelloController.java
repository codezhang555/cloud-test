package cn.itz.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author CodeZhang
 * @ProjectName cloud
 * @Package cn.itz.cloud.controller
 * @Version 1.0
 * @date 2021/1/3 18:57
 */
@RestController
public class UserHelloController {

    @Autowired
    DiscoveryClient discoveryClient;

    @GetMapping("/hello1")
    public String hello1() {
        HttpURLConnection con = null;
        try {
            URL url = new URL("http://localhost:8081/hello");
            con = ((HttpURLConnection) url.openConnection());
            if (con.getResponseCode() == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String s = br.readLine();
                br.close();
                return s;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    int count  = 0;

    @GetMapping("/hello2")
    public String hello2() {
        List<ServiceInstance> provider = discoveryClient.getInstances("provider");
        ServiceInstance instance = provider.get((count++) % provider.size());
        String host = instance.getHost();
        int port = instance.getPort();
        HttpURLConnection con = null;
        StringBuffer sb = new StringBuffer();
        sb.append("http://").append(host).append(":").append(port).append("/hello");
        try {
            URL url = new URL(sb.toString());
            con = ((HttpURLConnection) url.openConnection());
            if (con.getResponseCode() == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String s = br.readLine();
                br.close();
                return s;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
}