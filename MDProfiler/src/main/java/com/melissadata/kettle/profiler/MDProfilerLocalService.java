package com.melissadata.kettle.profiler;

import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;

public class MDProfilerLocalService extends MDProfilerCVService {
	public MDProfilerLocalService(MDProfilerData checkData, MDProfilerMeta checkMeta, VariableSpace space, LogChannelInterface log) throws KettleException {
		super(checkData, checkMeta, space, log);
		profiler = checkMeta.getProfiler();
	}

	@Override
	public void dispose() {
		if (profiler != null) {
			profiler.delete();
		}
	}

	@Override
	public void processRequests(List<MDProfilerRequest> requests, int queue) throws KettleException {
		for (MDProfilerRequest pr : requests) {
			profileRequests(pr);
		}
	}

	private void profileRequests(MDProfilerRequest request) {
		String[] inputFieldNames = checkData.inputMeta.getFieldNames();
		// Add record
		String val = "";
		for (int i = 0; i < inputFieldNames.length; i++) {
			if (checkMeta.getProfileRecords().get(inputFieldNames[i]) != null) {
				if (checkMeta.getProfileRecords().get(inputFieldNames[i]).isDoProfile()) {
					if (request.inputData[i] == null) {
						val = "";
					} else {
						try {
						val = request.inputMeta.getString(request.inputData, i);//request.inputData[i].toString();
						} catch (Exception e){
							System.out.println("Error reading value from input data. column name : " + checkMeta.getProfileRecords().get(inputFieldNames[i]).getColumnName());
							val = "value_read_error";
						}
//
					}
					log.logDebug("Profiler SetColumn - ColumnName:" + checkMeta.getProfileRecords().get(inputFieldNames[i]).getColumnName() + "  Field Value:" + val + "   Field DataType:" + request.inputMeta.getValueMeta(i));
					profiler.SetColumn(checkMeta.getProfileRecords().get(inputFieldNames[i]).getColumnName(), val);
				}
			}
		}
		profiler.AddRecord();
		for (String inputFieldName : inputFieldNames) {
			if (checkMeta.getProfileRecords().get(inputFieldName) != null) {
				if (checkMeta.getProfileRecords().get(inputFieldName).isDoProfile()) {
					request.resultCodes.put(checkMeta.getProfileRecords().get(inputFieldName).getResutName(), profiler.GetResults(inputFieldName));
				}
			}
		}
	}

	@Override
	public void saveReports() throws KettleException {
		// No reports to save
	}
}
