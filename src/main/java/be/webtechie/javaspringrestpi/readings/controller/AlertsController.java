package be.webtechie.javaspringrestpi.readings.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import be.webtechie.javaspringrestpi.readings.alert.Alert;
import be.webtechie.javaspringrestpi.readings.configuration.RaspberryPiTemperatureRecordedConfiguration;
import be.webtechie.javaspringrestpi.readings.model.Pair;

/**
 * {@link Controller} that handles the REST endpoints for the {@link Alert}s.
 * 
 * @author Dan Wiechert
 */
@Controller
@RequestMapping("/alerts")
public class AlertsController {
	@Autowired
	private RaspberryPiTemperatureRecordedConfiguration configuration;

	/**
	 * Lists all of the {@link Alert}s that are known.
	 * 
	 * @return The list of alerts.
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody List<String> list() {
		return getAlertInfos(null);
	}

	/**
	 * Lists all of the {@link Alert}s that are known with the provided name.
	 * 
	 * @param name
	 *            The name of the alert.
	 * @return The list of alerts.
	 */
	@RequestMapping(value = "/list/{name}", method = RequestMethod.GET)
	public @ResponseBody List<String> list(@PathVariable(value = "name") final String name) {
		return getAlertInfos(name == null ? "" : name);
	}

	private List<String> getAlertInfos(final String name) {
		final List<String> alertInfos = new ArrayList<>();
		for (final Alert alert : configuration.getAlerts()) {
			if (name == null || name.equals(alert.getName())) {
				alertInfos.add(alert.getInfo());
			}
		}
		return alertInfos;
	}

	/**
	 * Sets the provided alert to the on status.
	 * 
	 * @param name
	 *            The name of the alert.
	 */
	@RequestMapping(value = "/setOn/{name}", method = RequestMethod.GET)
	public @ResponseBody Alert setOnGet(@PathVariable(value = "name") final String name) {
		Alert rAlert = null;
		for (final Alert alert : configuration.getAlerts()) {
			if (alert.getName().equals(name)) {
				alert.setOn(true);
				rAlert=alert;
			}
		}
		return rAlert;
	}
	
	@RequestMapping(value = "/setOn/{name}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public void setOn(@PathVariable(value = "name") final String name) {
		for (final Alert alert : configuration.getAlerts()) {
			if (alert.getName().equals(name)) {
				alert.setOn(true);
			}
		}
	}

	/**
	 * Sets the provided alert to the off status.
	 * 
	 * @param name
	 *            The name of the alert.
	 */
	@RequestMapping(value = "/setOff/{name}", method = RequestMethod.GET)
	public @ResponseBody Alert setOffGet(@PathVariable(value = "name") final String name) {
		Alert rAlert = null;
		for (final Alert alert : configuration.getAlerts()) {
			if (alert.getName().equals(name)) {
				alert.setOn(false);
				rAlert=alert;
			}
		}
		return rAlert;
	}
	@RequestMapping(value = "/setOff/{name}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public void setOff(@PathVariable(value = "name") final String name) {
		for (final Alert alert : configuration.getAlerts()) {
			if (alert.getName().equals(name)) {
				alert.setOn(false);
			}
		}
	}

	/**
	 * Updates the provided alert.
	 * 
	 * @param name
	 *            The name of the alert.
	 * @param message
	 *            The alert's update message.
	 * @return The response.
	 */
	@RequestMapping(value = "/update/{name}", method = RequestMethod.PUT)
	@ResponseBody
	public HttpEntity<String> update(@PathVariable(value = "name") final String name, @RequestBody final String message) {
		final StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (final Alert alert : configuration.getAlerts()) {
			if (alert.getName().equals(name)) {
				final Pair<Boolean, String> updateStatus = alert.update(message);
				if (!updateStatus.getElement1()) {
					sb.append(prefix);
					prefix = ",";
					sb.append(updateStatus.getElement2());
				}
			}
		}

		final ResponseEntity<String> response;
		final String statuses = sb.toString();
		if (statuses.isEmpty()) {
			response = new ResponseEntity<String>(HttpStatus.OK);
		} else {
			response = new ResponseEntity<String>(statuses, HttpStatus.BAD_REQUEST);
		}
		return response;
	}
}
