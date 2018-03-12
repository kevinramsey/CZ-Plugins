package com.melissadata.kettle.profiler.ui;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMetaInterface;

import com.melissadata.kettle.profiler.data.ProfilerEnum.ColumnType;
import com.melissadata.kettle.profiler.data.ProfilerEnum.ExpectedContent;

public class ProfileRecord {
	private boolean				doProfile;
	private boolean				doPassThrough;
	private boolean				doResults;
	private String				columnName;
	private String				resultName;
	private ExpectedContent		expectedContent;
	private ColumnType			columnType;
	private boolean				setLength;
	private String				length;
	private boolean				setPrecision;
	private String				precision;
	private boolean				setScale;
	private String				scale;
	private boolean				setDefaultValue;
	private String				defaultValue;
	private boolean				setBounds;
	private String				upperBound;
	private String				lowerBound;
	// regex
	private boolean				setCustomPattern;
	private String				customPattern;
	private ValueMetaInterface	vmi;

	public ProfileRecord(String colname) {
		doProfile = false;
		doPassThrough = false;
		doResults = false;
		columnName = colname;
		resultName = "MD_" + colname;
		expectedContent = ExpectedContent.String;
		columnType = ColumnType.String;
		setLength = false;
		length = "";
		setPrecision = false;
		precision = "";
		setScale = false;
		scale = "";
		setDefaultValue = false;
		defaultValue = "";
		setBounds = false;
		upperBound = "";
		lowerBound = "";
		// regex
		setCustomPattern = false;
		customPattern = "";
	}

	public ProfileRecord(ValueMetaInterface vmi, boolean profile, boolean passthru, boolean results, boolean setDefault, String defaultVal, boolean setBounds, String uBounds, String lBounds, boolean setCustom, String customPattern, String colname,
			ExpectedContent content, ColumnType columnType, boolean setLength, String length, boolean setPrecision, String percision, boolean setScale, String scale) {
		this.vmi = vmi;
		doProfile = profile;
		doPassThrough = passthru;
		doResults = results;
		columnName = colname;
		resultName = "MD_" + colname;
		expectedContent = content;
		this.columnType = columnType;
		this.setLength = setLength;
		this.length = length;
		this.setPrecision = setPrecision;
		this.precision = percision;
		this.setScale = setScale;
		this.scale = scale;
		setDefaultValue = setDefault;
		defaultValue = defaultVal;
		this.setBounds = setBounds;
		upperBound = uBounds;
		lowerBound = lBounds;
		// regex
		setCustomPattern = setCustom;
		this.customPattern = customPattern;
	}

	@Override
	public ProfileRecord clone() {
		ProfileRecord clonedRecord = new ProfileRecord("");
		clonedRecord.vmi = vmi;
		clonedRecord.doProfile = doProfile;
		clonedRecord.doPassThrough = doPassThrough;
		clonedRecord.doResults = doResults;
		clonedRecord.columnName = columnName;
		clonedRecord.expectedContent = expectedContent;
		clonedRecord.columnType = columnType;
		clonedRecord.setLength = setLength;
		clonedRecord.length = length;
		clonedRecord.setPrecision = setPrecision;
		clonedRecord.precision = precision;
		clonedRecord.setScale = setScale;
		clonedRecord.scale = scale;
		clonedRecord.resultName = resultName;
		clonedRecord.setDefaultValue = setDefaultValue;
		clonedRecord.defaultValue = defaultValue;
		clonedRecord.setBounds = setBounds;
		clonedRecord.upperBound = upperBound;
		clonedRecord.lowerBound = lowerBound;
		clonedRecord.setCustomPattern = setCustomPattern;
		clonedRecord.customPattern = customPattern;
		return clonedRecord;
	}

	public String getColumnName() {
		if (Const.isEmpty(columnName))
			return "EMPTY_COLNAME";
		return columnName;
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public String getCustomPattern() {
		return customPattern;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public ExpectedContent getExpectedContent() {
		return expectedContent;
	}

	public String getLength() {
		return length;
	}

	public String getLowerBound() {
		return lowerBound;
	}

	public String getPrecision() {
		return precision;
	}

	public String getResutName() {
		return resultName;
	}

	public String getScale() {
		return scale;
	}

	public String getSettingsString() {
		String settings = "";
		if (setScale) {
			settings += "Scale:" + scale + ", ";
		}
		if (setPrecision) {
			settings += "Precision:" + precision + ", ";
		}
		if (setLength) {
			settings += "Length:" + length + ", ";
		}
		if (setDefaultValue) {
			settings += "Default:" + defaultValue + ", ";
		}
		if (setBounds) {
			settings += "Bounds[upper:" + upperBound + ", lower:" + lowerBound + "]" + ", ";
		}
		if (setCustomPattern) {
			settings += "Pattern:" + customPattern + ", ";
		}
		// remove trailing ', '
		if (settings.length() > 3) {
			settings = settings.substring(0, settings.length() - 2);
		}
		return settings;
	}

	public String getUpperBound() {
		return upperBound;
	}

	public ValueMetaInterface getVmi() {
		return vmi;
	}

	public boolean isDoPassThrough() {
		return doPassThrough;
	}

	public boolean isDoProfile() {
		return doProfile;
	}

	public boolean isDoResults() {
		return doResults;
	}

	public boolean isSetBounds() {
		return setBounds;
	}

	public boolean isSetCustomPattern() {
		return setCustomPattern;
	}

	public boolean isSetDefaultValue() {
		return setDefaultValue;
	}

	public boolean isSetLength() {
		return setLength;
	}

	public boolean isSetPrecision() {
		return setPrecision;
	}

	public boolean isSetScale() {
		return setScale;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setColumnType(String colType) {
		columnType = ColumnType.decode(colType);
	}

	public void setCustomPattern(String customPattern) {
		this.customPattern = customPattern;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDoPassThrough(boolean doPassThrough) {
		this.doPassThrough = doPassThrough;
	}

	public void setDoProfile(boolean doProfile) {
		this.doProfile = doProfile;
	}

	public void setDoResults(boolean doResults) {
		this.doResults = doResults;
	}

	public void setExpectedContent(String expectedContent) {
		this.expectedContent = ExpectedContent.decode(expectedContent);
	}

	public void setLength(String length) {
		this.length = length;
	}

	public void setLowerBound(String lowerBound) {
		this.lowerBound = lowerBound;
	}

	public void setPrecision(String percision) {
		this.precision = percision;
	}

	public void setResutName(String resutName) {
		resultName = resutName;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public void setSetBounds(boolean setBounds) {
		this.setBounds = setBounds;
	}

	public void setSetCustomPattern(boolean setCustomPattern) {
		this.setCustomPattern = setCustomPattern;
	}

	public void setSetDefaultValue(boolean setDefaultValue) {
		this.setDefaultValue = setDefaultValue;
	}

	public void setSetLength(boolean setLength) {
		this.setLength = setLength;
	}

	public void setSetPrecision(boolean doPrecision) {
		setPrecision = doPrecision;
	}

	public void setSetScale(boolean setScale) {
		this.setScale = setScale;
	}

	public void setUpperBound(String upperBound) {
		this.upperBound = upperBound;
	}

	public void setVmi(ValueMetaInterface vmi) {
		this.vmi = vmi;
	}

	@Override
	public String toString() {
		return "ProfileRecord [doProfile=" + doProfile + ", doPassThrough=" + doPassThrough + ", doResults=" + doResults + ", columnName=" + columnName + ", resultName=" + resultName + ", expectedContent=" + expectedContent + ", columnType="
				+ columnType + ", setLength=" + setLength + ", length=" + length + ", setPrecision=" + setPrecision + ", percision=" + precision + ", setScale=" + setScale + ", scale=" + scale + ", setDefaultValue=" + setDefaultValue
				+ ", defaultValue=" + defaultValue + ", setBounds=" + setBounds + ", upperBound=" + upperBound + ", lowerBound=" + lowerBound + ", setCustomPattern=" + setCustomPattern + ", customPattern=" + customPattern + ", vmi=" + vmi + "]";
	}
}
