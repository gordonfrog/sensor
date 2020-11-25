package be.webtechie.javaspringrestpi.readings.controller;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import be.webtechie.javaspringrestpi.readings.alert.Alert;
import be.webtechie.javaspringrestpi.readings.configuration.RaspberryPiTemperatureRecordedConfiguration;
import be.webtechie.javaspringrestpi.readings.model.Sensor;

/**
 * {@link Controller} that handles the REST endpoints for the {@link Sensor}s.
 * 
 * @author Dan Wiechert
 */
@Controller
@EnableAsync
@EnableScheduling
@RequestMapping("/api")
public class ApiController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = "/dispatcher/temperature/{id}/{temp}", method = RequestMethod.POST)
	public @ResponseBody String temp(@PathVariable(value = "id") final String id, @PathVariable(value = "temp") final String temp) throws Exception {
		logger.info("Sensor Received and Responding..");
		return buildRandomizedTemperature(id);
	}
	
	private String buildRandomizedTemperature(String uuid) {
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        sb.append(uuid);
        sb.append("/");

        Random rand = new Random();
        int r = rand.nextInt(40);
        // NOTE: r = 21
        double result = r * 1.15;
        // NOTE: result = 21 * 1.15 = 24.15
        BigDecimal value = new BigDecimal(result, MathContext.DECIMAL32);
        sb.append(value.toString());

        // NOTE:  http://xdevicesdev.home:8000/api/dispatcher/temperature/b2b4c9d4-8990-41cf-9dfc-c659dd28a8c3/24.15
        return sb.toString();
    }
}
