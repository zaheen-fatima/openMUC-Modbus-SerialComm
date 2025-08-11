package org.openmuc.framework.app.modbusRTU;

import org.openmuc.framework.data.*;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.dataaccess.*;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@Component(immediate = true)
public class ModbusRtu {

    private static final Logger logger = LoggerFactory.getLogger(ModbusRtu.class);
    private static final String APP_NAME = "ModbusRTU App";

    private DataAccessService dataAccessService;
    private Channel readChannel;
    private Channel writeChannel;
    private RecordListener listener;

    @Reference
    public void setDataAccessService(DataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Activate
    public void activate() {
        logger.info("Activating {}", APP_NAME);

        try {
            readChannel = dataAccessService.getChannel("holding1");
            writeChannel = dataAccessService.getChannel("holding2");

            if (readChannel == null || writeChannel == null) {
                logger.error("One or both channels are null!");
                return;
            }

            listener = new RecordListener() {
                @Override
                public void newRecord(Record record) {
                    if (record != null && record.getValue() != null) {
                        Value value = record.getValue();

                        if (value.getValueType() == ValueType.INTEGER) {
                            try {
                                writeChannel.write(new IntValue(value.asInt()));
                                logger.info("Received from holding1: {}, written to holding2: {}", value, value);
                            } catch (Exception e) {
                                logger.error("Failed to write to holding2: {}", e.getMessage());
                            }
                        } else {
                            logger.warn("Received non-integer value on holding1");
                        }
                    }
                }
            };

            readChannel.addListener(listener);
            logger.info("Listener registered on holding1");

        } catch (Exception e) {
            logger.error("Error during activation: {}", e.getMessage());
        }
    }

    @Deactivate
    public void deactivate() {
        try {
            if (readChannel != null && listener != null) {
                readChannel.removeListener(listener);
                logger.info("Listener removed from holding1");
            }
        } catch (Exception e) {
            logger.error("Error during deactivation: {}", e.getMessage());
        }
    }
}