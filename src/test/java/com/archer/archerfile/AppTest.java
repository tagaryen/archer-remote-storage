package com.archer.archerfile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.archer.rs.ArcherRSClient;
import com.archer.xjson.JavaTypeRef;

public class AppTest 
{

	static AtomicInteger c = new AtomicInteger(0);
	
    public static void main( String[] args )
    {
//    	final ArcherRSClient acli = new ArcherRSClient("123.57.55.232", 9611, "u&*1l)+yv1%*:^tg".getBytes());
    	final ArcherRSClient acli = new ArcherRSClient("127.0.0.1", 9611, "u&*1l)+yv1%*:^tg".getBytes());
    	
    	
    	LocalDateTime now = LocalDateTime.now();
    	final Base<Xyer> xy = new Base<>(new Xyer(1996, 11.17D, "xuyi", now), "xuyi shi da shuai ge"); 

    	
    	acli.save("xuyia", xy);
    	Base<Xyer> xycpy = acli.get("xuyia", new JavaTypeRef<Base<Xyer>>() {});
    	System.out.println("get "+xycpy.getData().getD().toString());

    	/**
		ExecutorService pool = Executors.newFixedThreadPool(2);
        List<CompletableFuture<Void>> cfList = new ArrayList<>();
    	int total = 100;
    	long t0 = System.currentTimeMillis();
		for(int i = 0; i < total; i++) {
			CompletableFuture<Void> cf = CompletableFuture.supplyAsync(new Supplier<Void> () {
				@Override
				public Void get() {
					try {
				    	acli.save("xuyia", xy);
				    	Base<Xyer> xycpy = acli.get("xuyia", new JavaTypeRef<Base<Xyer>>() {});
					} catch(Exception e) {
						c.incrementAndGet();
					}
					return null;
				}
				
			}, pool);
			cfList.add(cf);
		}
        CompletableFuture<Void> all = CompletableFuture.allOf(cfList.toArray(new CompletableFuture[0]));
        all.join();
    	long t1 = System.currentTimeMillis();
    	System.out.println("cost = " + (t1 - t0));
    	System.out.println("err count = " + c.get());
    	*/
    	
    	
    }
}
