package be.webtechie.javaspringrestpi.readings.alert.impls;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import be.webtechie.javaspringrestpi.readings.alert.AbstractAlert;
import be.webtechie.javaspringrestpi.readings.alert.configs.CsvAlertConfig;
import be.webtechie.javaspringrestpi.readings.model.Pair;
import be.webtechie.javaspringrestpi.readings.model.Sensor;


/**
 * {@link Alert} that writes the temperate data to a csv file.
 * <p/>
 * To {@link #update(String)} this alert, the update String should be a JSON representation of the {@link CsvAlertConfig} class which supports:
 * <p>
 * <table border="1">
 * <tr>
 * <th>Variable</th>
 * <th>Type</th>
 * <th>Default</th>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>String</td>
 * <td>sensor-data.csv</td>
 * </tr>
 * <tr>
 * <td>delimiter</td>
 * <td>char</td>
 * <td>{@code ,}</td>
 * </tr>
 * </table>
 * <p/>
 * 
 * @author Dan Wiechert
 */
@Component
public class CsvAlert extends AbstractAlert {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	private static final String NAME = "CsvAlert";
	private CsvAlertConfig config = new CsvAlertConfig();

	@Override
	public void alert(final List<Sensor> sensors) {
		final File file = new File(config.getFilename());
		final StringBuilder builder = new StringBuilder();
		logger.info("*** Write Alert CSV START ***");
		for (final Sensor sensor : sensors) {
			// @formatter:off
			builder
				.append(ZonedDateTime.now().format(DATE_FORMATTER)).append(config.getDelimiter())
				.append(sensor.getName()).append(config.getDelimiter())
				.append(sensor.getSerialId()).append(config.getDelimiter())
				.append(sensor.getTempC()).append(config.getDelimiter())
				.append(sensor.getTempF()).append("\n");
			// @formatter:on
		}
		try {
			logger.info("file: "+file);
			FileUtils.write(file, builder, true);
		} catch (final IOException e) {
			LOGGER.error("CsvAlert unable to write to file {} lines:\n{}", file, builder);
		}
		logger.info("*** Write Alert CSV DONE ***");
	}

	@Override
	public Pair<Boolean, String> update(final String message) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			config = mapper.readValue(message, CsvAlertConfig.class);
		} catch (final IOException e) {
			LOGGER.error("Unable to parse JSON.", e);
			return Pair.of(Boolean.FALSE, "Unable to parse JSON - " + e.getMessage());
		}
		return Pair.of(Boolean.TRUE, "");
	}

	@Override
	public String getName() {
		return NAME;
	}
}
