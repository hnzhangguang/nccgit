package cn.bingoogolapple.qrcode.zbar;

import java.util.ArrayList;
import java.util.List;


public class BarcodeFormat {
	public static final BarcodeFormat NONE = new BarcodeFormat(0, "NONE");
	public static final BarcodeFormat PARTIAL = new BarcodeFormat(1, "PARTIAL");
	public static final BarcodeFormat EAN8 = new BarcodeFormat(8, "EAN8");
	public static final BarcodeFormat UPCE = new BarcodeFormat(9, "UPCE");
	public static final BarcodeFormat ISBN10 = new BarcodeFormat(10, "ISBN10");
	public static final BarcodeFormat UPCA = new BarcodeFormat(12, "UPCA");
	public static final BarcodeFormat EAN13 = new BarcodeFormat(13, "EAN13");
	public static final BarcodeFormat ISBN13 = new BarcodeFormat(14, "ISBN13");
	public static final BarcodeFormat I25 = new BarcodeFormat(25, "I25");
	public static final BarcodeFormat DATABAR = new BarcodeFormat(34, "DATABAR");
	public static final BarcodeFormat DATABAR_EXP = new BarcodeFormat(35, "DATABAR_EXP");
	public static final BarcodeFormat CODABAR = new BarcodeFormat(38, "CODABAR");
	public static final BarcodeFormat CODE39 = new BarcodeFormat(39, "CODE39");
	public static final BarcodeFormat PDF417 = new BarcodeFormat(57, "PDF417");
	public static final BarcodeFormat QRCODE = new BarcodeFormat(64, "QRCODE");
	public static final BarcodeFormat CODE93 = new BarcodeFormat(93, "CODE93");
	public static final BarcodeFormat CODE128 = new BarcodeFormat(128, "CODE128");
	static final List<BarcodeFormat> ALL_FORMAT_LIST = new ArrayList();
	static final List<BarcodeFormat> ONE_DIMENSION_FORMAT_LIST;
	static final List<BarcodeFormat> TWO_DIMENSION_FORMAT_LIST;
	static final List<BarcodeFormat> HIGH_FREQUENCY_FORMAT_LIST;
	
	static {
		ALL_FORMAT_LIST.add(PARTIAL);
		ALL_FORMAT_LIST.add(EAN8);
		ALL_FORMAT_LIST.add(UPCE);
		
		ALL_FORMAT_LIST.add(UPCA);
		ALL_FORMAT_LIST.add(EAN13);
		ALL_FORMAT_LIST.add(ISBN13);
		ALL_FORMAT_LIST.add(I25);
		
		ALL_FORMAT_LIST.add(DATABAR_EXP);
		ALL_FORMAT_LIST.add(CODABAR);
		ALL_FORMAT_LIST.add(CODE39);
		ALL_FORMAT_LIST.add(PDF417);
		ALL_FORMAT_LIST.add(QRCODE);
		ALL_FORMAT_LIST.add(CODE93);
		ALL_FORMAT_LIST.add(CODE128);
		
		
		ONE_DIMENSION_FORMAT_LIST = new ArrayList();
		
		
		ONE_DIMENSION_FORMAT_LIST.add(PARTIAL);
		ONE_DIMENSION_FORMAT_LIST.add(EAN8);
		ONE_DIMENSION_FORMAT_LIST.add(UPCE);
		
		ONE_DIMENSION_FORMAT_LIST.add(UPCA);
		ONE_DIMENSION_FORMAT_LIST.add(EAN13);
		ONE_DIMENSION_FORMAT_LIST.add(ISBN13);
		ONE_DIMENSION_FORMAT_LIST.add(I25);
		
		ONE_DIMENSION_FORMAT_LIST.add(DATABAR_EXP);
		ONE_DIMENSION_FORMAT_LIST.add(CODABAR);
		ONE_DIMENSION_FORMAT_LIST.add(CODE39);
		ONE_DIMENSION_FORMAT_LIST.add(PDF417);
		ONE_DIMENSION_FORMAT_LIST.add(CODE93);
		ONE_DIMENSION_FORMAT_LIST.add(CODE128);
		
		
		TWO_DIMENSION_FORMAT_LIST = new ArrayList();
		
		
		TWO_DIMENSION_FORMAT_LIST.add(PDF417);
		TWO_DIMENSION_FORMAT_LIST.add(QRCODE);
		
		
		HIGH_FREQUENCY_FORMAT_LIST = new ArrayList();
		
		
		HIGH_FREQUENCY_FORMAT_LIST.add(QRCODE);
		HIGH_FREQUENCY_FORMAT_LIST.add(ISBN13);
		HIGH_FREQUENCY_FORMAT_LIST.add(UPCA);
		HIGH_FREQUENCY_FORMAT_LIST.add(EAN13);
		HIGH_FREQUENCY_FORMAT_LIST.add(CODE128);
	}
	
	private int mId;
	private String mName;
	
	private BarcodeFormat(int id, String name) {
		this.mId = id;
		this.mName = name;
	}
	
	public static BarcodeFormat getFormatById(int id) {
		for (BarcodeFormat format : ALL_FORMAT_LIST) {
			if (format.getId() == id) {
				return format;
			}
		}
		return NONE;
	}
	
	public int getId() {
		return this.mId;
	}
	
	public String getName() {
		return this.mName;
	}
}
