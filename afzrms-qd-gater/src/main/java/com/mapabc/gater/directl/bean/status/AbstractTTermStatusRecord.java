package com.mapabc.gater.directl.bean.status;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * AbstractTTermStatusRecord generated by MyEclipse Persistence Tools
 */

public abstract class AbstractTTermStatusRecord implements java.io.Serializable {

	/**
	 * 状态关联的位置ID
	 */
	private String uuid;
	/**
	 * 设备ID
	 */
	private String deviceId;
	/**
	 * GPS时间
	 */
	private Timestamp gpsTime;
	/**
	 * 定位状态,默认已定位
	 */
	private String locate="1";
	/**
	 * ACC状态
	 */
	private String acc;
	/**
	 * 空重车状态
	 */
	private String fullEmpty;
	/**
	 * 罐车状态
	 */
	private String tank;
	/**
	 * 倒车状态
	 */
	private String backCar;
	/**
	 * 刹车状态
	 */
	private String brake;
	/**
	 * 车门状态
	 */
	private String carDoor;
	/**
	 * 前车门状态
	 */
	private String frontCarDoor;
	/**
	 * 后车门状态
	 */
	private String rearCarDoor;
	/**
	 * 设防状态
	 */
	private String fortification;
	/**
	 * 天线状态
	 */
	private String antenna;
	/**
	 * 主电源状态
	 */
	private String mainPower;
	/**
	 * 油电路状态
	 */
	private String oilElec;
	/**
	 * GPS模块状态
	 */
	private String gpsModule;
	/**
	 * GSM模块状态
	 */
	private String gsmModule;
	/**
	 * 后备电池
	 */
	private String backupBattery;
	/**
	 * 发动机
	 */
	private String engine;
	/**
	 * 轮胎状态
	 */
	private String tire;
	/**
	 * 大灯状态
	 */
	private String bigLight;
	/**
	 * 左灯状态
	 */
	private String leftLight;
	/**
	 * 右灯状态
	 */
	private String rightLight;
	/**
	 * 刹车灯状态
	 */
	private String brakeLight;
	/**
	 * 远光灯状态
	 */
	private String farLight;
	/**
	 * 近光灯状态
	 */
	private String nearLight;
	/**
	 * 前雾灯状态
	 */
	private String frontFogLight;
	/**
	 * 后雾灯状态
	 */
	private String backFogLight;
	/**
	 * 油箱状态
	 */
	private String oilBox;
	/**
	 * 货箱状态
	 */
	private String goodsBox;
	/**
	 * 手柄状态
	 */
	private String handle;
	/**
	 * 显示屏状态
	 */
	private String display;
	/**
	 * 图像采集器状态
	 */
	private String imageCollector;
	/**
	 * 计价器状态
	 */
	private String meter;
	/**
	 * 语音拨号器状态
	 */
	private String voiceDialer;
	/**
	 * 通话状态
	 */
	private String call;
	 
	/**
	 * 锁检测状态
	 */
	private String keyCheck;
	/**
	 * 停车状态
	 */
	private String stopCar;
	/**
	 * 登退签状态
	 */
	private String loginOut;
	/**
	 * 停车时间
	 */
	private String stopCarTime;
	/**
	 * CPU状态
	 */
	private String cpu;
	/**
	 * 内存状态
	 */
	private String memory;

	/**
	 * SD卡状态
	 */
	private String sdCard;
	/**
	 * 打印机状态
	 */
	private String printer;
	/**
	 * 是否定时
	 */
	private String isTimerLocate;
	/**
	 * 是否定距离
	 */
	private String isDistanceLocate;
	/**
	 * 摄像头状态
	 */
	private String camera;
	/**
	 * 空调状态
	 */
	private String conditioning;
	/**
	 * 温度
	 */
	private Float temperator;
	/**
	 * 熄火状态
	 */
	private String flameOut;
	/**
	 * 油耗量
	 */
	private Float oilMass;
	/**
	 * 停车时间间隔
	 */
	private String stopCarTimeInterval;
	/**
	 * 上报温度的通道号
	 */
	private String temeratureRouteNum;
	/**
	 * 耗油量
	 */
	private Float oilUsed;
	/**
	 * 加油量
	 */
	private Float oilAdded;

	/**
	 * 空调状态
	 * 
	 * @return the conditioning
	 */
	public String getConditioning() {
		return this.conditioning;
	}

	/**
	 * 空调状态
	 * 
	 * @param conditioning
	 *            the conditioning to set
	 */
	public void setConditioning(String conditioning) {
		this.conditioning = conditioning;
	}

	/** default constructor */
	public AbstractTTermStatusRecord() {
	}

	/**
	 * 定位状态
	 * 
	 * @return
	 * @author 
	 */
	public String getLocate() {
		return this.locate;
	}

	/**
	 * 定位状态
	 * 
	 * @param locate
	 * @author 
	 */
	public void setLocate(String locate) {
		this.locate = locate;
	}

	/**
	 * ACC状态
	 * 
	 * @return
	 * @author 
	 */
	public String getAcc() {
		return this.acc;
	}

	/**
	 * ACC状态
	 * 
	 * @param acc
	 * @author 
	 */
	public void setAcc(String acc) {
		this.acc = acc;
	}

	/**
	 * 空/重车状态
	 * 
	 * @return
	 * @author 
	 */
	public String getFullEmpty() {
		return this.fullEmpty;
	}

	/**
	 * 空/重车状态
	 * 
	 * @param fullEmpty
	 * @author 
	 */
	public void setFullEmpty(String fullEmpty) {
		this.fullEmpty = fullEmpty;
	}

	/**
	 * 罐车状态
	 * 
	 * @return
	 * @author 
	 */
	public String getTank() {
		return this.tank;
	}

	/**
	 * 罐车状态
	 * 
	 * @param tank
	 * @author 
	 */
	public void setTank(String tank) {
		this.tank = tank;
	}

	/**
	 * 倒车状态
	 * 
	 * @return
	 * @author 
	 */
	public String getBackCar() {
		return this.backCar;
	}

	/**
	 * 倒车状态
	 * 
	 * @param backCar
	 * @author 
	 */
	public void setBackCar(String backCar) {
		this.backCar = backCar;
	}

	/**
	 * 刹车状态
	 * 
	 * @return
	 * @author 
	 */
	public String getBrake() {
		return this.brake;
	}

	/**
	 * 刹车状态
	 * 
	 * @param brake
	 * @author 
	 */
	public void setBrake(String brake) {
		this.brake = brake;
	}

	/**
	 * 车门状态
	 * 
	 * @return
	 * @author 
	 */
	public String getCarDoor() {
		return this.carDoor;
	}

	/**
	 * 车门状态
	 * 
	 * @param carDoor
	 * @author 
	 */
	public void setCarDoor(String carDoor) {
		this.carDoor = carDoor;
	}

	/**
	 * 前车门状态
	 * 
	 * @return
	 * @author 
	 */
	public String getFrontCarDoor() {
		return this.frontCarDoor;
	}

	/**
	 * 前车门状态
	 * 
	 * @param frontCarDoor
	 * @author 
	 */
	public void setFrontCarDoor(String frontCarDoor) {
		this.frontCarDoor = frontCarDoor;
	}

	/**
	 * 后车门状态
	 * 
	 * @return
	 * @author 
	 */
	public String getRearCarDoor() {
		return this.rearCarDoor;
	}

	/**
	 * 后车门状态
	 * 
	 * @param rearCarDoor
	 * @author 
	 */
	public void setRearCarDoor(String rearCarDoor) {
		this.rearCarDoor = rearCarDoor;
	}

	/**
	 * 设防状态
	 * 
	 * @return
	 * @author 
	 */
	public String getFortification() {
		return this.fortification;
	}

	/**
	 * 设防状态
	 * 
	 * @param fortification
	 * @author 
	 */
	public void setFortification(String fortification) {
		this.fortification = fortification;
	}

	/**
	 * 天线状态
	 * 
	 * @return
	 * @author 
	 */
	public String getAntenna() {
		return this.antenna;
	}

	/**
	 * 天线状态
	 * 
	 * @param antenna
	 * @author 
	 */
	public void setAntenna(String antenna) {
		this.antenna = antenna;
	}

	/**
	 * 主电源状态
	 * 
	 * @return
	 * @author 
	 */
	public String getMainPower() {
		return this.mainPower;
	}

	/**
	 * 主电源状态
	 * 
	 * @param mainPower
	 * @author 
	 */
	public void setMainPower(String mainPower) {
		this.mainPower = mainPower;
	}

	/**
	 * 油/电路状态
	 * 
	 * @return
	 * @author 
	 */
	public String getOilElec() {
		return this.oilElec;
	}

	/**
	 * 油/电路状态
	 * 
	 * @param oilElec
	 * @author 
	 */
	public void setOilElec(String oilElec) {
		this.oilElec = oilElec;
	}

	/**
	 * 
	 * @return
	 * @author 
	 */
	public String getGpsModule() {
		return this.gpsModule;
	}

	/**
	 * GPS模块状态
	 * 
	 * @param gpsModule
	 * @author 
	 */
	public void setGpsModule(String gpsModule) {
		this.gpsModule = gpsModule;
	}

	/**
	 * GSM模块状态
	 * 
	 * @return
	 * @author 
	 */
	public String getGsmModule() {
		return this.gsmModule;
	}

	/**
	 * GSM模块状态
	 * 
	 * @param gsmModule
	 * @author 
	 */
	public void setGsmModule(String gsmModule) {
		this.gsmModule = gsmModule;
	}

	/**
	 * 后备电池状态
	 * 
	 * @return
	 * @author 
	 */
	public String getBackupBattery() {
		return this.backupBattery;
	}

	/**
	 * 后备电池状态
	 * 
	 * @param backupBattery
	 * @author 
	 */
	public void setBackupBattery(String backupBattery) {
		this.backupBattery = backupBattery;
	}

	/**
	 * 发动机（引擎）状态
	 * 
	 * @return
	 * @author 
	 */
	public String getEngine() {
		return this.engine;
	}

	/**
	 * 发动机（引擎）状态
	 * 
	 * @param engine
	 * @author 
	 */
	public void setEngine(String engine) {
		this.engine = engine;
	}

	/**
	 * 轮胎状态
	 * 
	 * @return
	 * @author 
	 */
	public String getTire() {
		return this.tire;
	}

	/**
	 * 轮胎状态
	 * 
	 * @param tire
	 * @author 
	 */
	public void setTire(String tire) {
		this.tire = tire;
	}

	/**
	 * 大灯状态
	 * 
	 * @return
	 * @author 
	 */
	public String getBigLight() {
		return this.bigLight;
	}

	/**
	 * 大灯状态
	 * 
	 * @param bigLight
	 * @author 
	 */
	public void setBigLight(String bigLight) {
		this.bigLight = bigLight;
	}

	/**
	 * 左灯状态
	 * 
	 * @return
	 * @author 
	 */
	public String getLeftLight() {
		return this.leftLight;
	}

	/**
	 * 左灯状态
	 * 
	 * @param leftLight
	 * @author 
	 */
	public void setLeftLight(String leftLight) {
		this.leftLight = leftLight;
	}

	/**
	 * 右灯状态
	 * 
	 * @return
	 * @author 
	 */
	public String getRightLight() {
		return this.rightLight;
	}

	/**
	 * 右灯状态
	 * 
	 * @param rightLight
	 * @author 
	 */
	public void setRightLight(String rightLight) {
		this.rightLight = rightLight;
	}

	/**
	 * 刹车灯状态
	 * 
	 * @return
	 * @author 
	 */
	public String getBrakeLight() {
		return this.brakeLight;
	}

	/**
	 * 刹车灯状态
	 * 
	 * @param brakeLight
	 * @author 
	 */
	public void setBrakeLight(String brakeLight) {
		this.brakeLight = brakeLight;
	}

	/**
	 * 远光灯状态
	 * 
	 * @return
	 * @author 
	 */
	public String getFarLight() {
		return this.farLight;
	}

	/**
	 * 远光灯状态
	 * 
	 * @param farLight
	 * @author 
	 */
	public void setFarLight(String farLight) {
		this.farLight = farLight;
	}

	/**
	 * 近光灯状态
	 * 
	 * @return
	 * @author 
	 */
	public String getNearLight() {
		return this.nearLight;
	}

	/**
	 * 近光灯状态
	 * 
	 * @param nearLight
	 * @author 
	 */
	public void setNearLight(String nearLight) {
		this.nearLight = nearLight;
	}

	/**
	 * 前雾灯状态
	 * 
	 * @return
	 * @author 
	 */
	public String getFrontFogLight() {
		return this.frontFogLight;
	}

	/**
	 * 前雾灯状态
	 * 
	 * @param frontFogLight
	 * @author 
	 */
	public void setFrontFogLight(String frontFogLight) {
		this.frontFogLight = frontFogLight;
	}

	/**
	 * 后雾灯状态
	 * 
	 * @return
	 * @author 
	 */
	public String getBackFogLight() {
		return this.backFogLight;
	}

	/**
	 * 后雾灯状态
	 * 
	 * @param backFogLight
	 * @author 
	 */
	public void setBackFogLight(String backFogLight) {
		this.backFogLight = backFogLight;
	}

	/**
	 * 油箱状态
	 * 
	 * @return
	 * @author 
	 */
	public String getOilBox() {
		return this.oilBox;
	}

	/**
	 * 油箱状态
	 * 
	 * @param oilBox
	 * @author 
	 */
	public void setOilBox(String oilBox) {
		this.oilBox = oilBox;
	}

	/**
	 * 货箱状态
	 * 
	 * @return
	 * @author 
	 */
	public String getGoodsBox() {
		return this.goodsBox;
	}

	/**
	 * 货箱状态
	 * 
	 * @param goodsBox
	 * @author 
	 */
	public void setGoodsBox(String goodsBox) {
		this.goodsBox = goodsBox;
	}

	/**
	 * 手柄状态
	 * 
	 * @return
	 * @author 
	 */
	public String getHandle() {
		return this.handle;
	}

	/**
	 * 手柄状态
	 * 
	 * @param handle
	 * @author 
	 */
	public void setHandle(String handle) {
		this.handle = handle;
	}

	/**
	 * 显示屏状态
	 * 
	 * @return
	 * @author 
	 */
	public String getDisplay() {
		return this.display;
	}

	/**
	 * 显示屏状态
	 * 
	 * @param display
	 * @author 
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * 图像采集器状态（摄像头）
	 * 
	 * @return
	 * @author 
	 */
	public String getImageCollector() {
		return this.imageCollector;
	}

	/**
	 * 图像采集器状态（摄像头）
	 * 
	 * @param imageCollector
	 * @author 
	 */
	public void setImageCollector(String imageCollector) {
		this.imageCollector = imageCollector;
	}

	/**
	 * 计价器状态
	 * 
	 * @return
	 * @author 
	 */
	public String getMeter() {
		return this.meter;
	}

	/**
	 * 计价器状态
	 * 
	 * @param meter
	 * @author 
	 */
	public void setMeter(String meter) {
		this.meter = meter;
	}

	/**
	 * 语音拨号状态
	 * 
	 * @return
	 * @author 
	 */
	public String getVoiceDialer() {
		return this.voiceDialer;
	}

	/**
	 * 语音拨号状态
	 * 
	 * @param voiceDialer
	 * @author 
	 */
	public void setVoiceDialer(String voiceDialer) {
		this.voiceDialer = voiceDialer;
	}

	/**
	 * 通话状态
	 * 
	 * @return
	 * @author 
	 */
	public String getCall() {
		return this.call;
	}

	/**
	 * 通话状态
	 * 
	 * @param call
	 * @author 
	 */
	public void setCall(String call) {
		this.call = call;
	}
 
	/**
	 * 锁检测状态
	 * 
	 * @return
	 * @author 
	 */
	public String getKeyCheck() {
		return this.keyCheck;
	}

	/**
	 * 锁检测状态
	 * 
	 * @param keyCheck
	 * @author 
	 */
	public void setKeyCheck(String keyCheck) {
		this.keyCheck = keyCheck;
	}

	/**
	 * 停车状态
	 * 
	 * @return
	 * @author 
	 */
	public String getStopCar() {
		return this.stopCar;
	}

	/**
	 * 停车状态
	 * 
	 * @param stopCar
	 * @author 
	 */
	public void setStopCar(String stopCar) {
		this.stopCar = stopCar;
	}

	/**
	 * 登陆或退签状态
	 * 
	 * @return
	 * @author 
	 */
	public String getLoginOut() {
		return this.loginOut;
	}

	/**
	 * 登陆或退签状态
	 * 
	 * @param loginOut
	 * @author 
	 */
	public void setLoginOut(String loginOut) {
		this.loginOut = loginOut;
	}

	/**
	 * 停车时间
	 * 
	 * @return
	 * @author 
	 */
	public String getStopCarTime() {
		return this.stopCarTime;
	}

	/**
	 * 停车时间
	 * 
	 * @param stopCarTime
	 * @author 
	 */
	public void setStopCarTime(String stopCarTime) {
		this.stopCarTime = stopCarTime;
	}

	/**
	 * 设备ID
	 * 
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return this.deviceId;
	}

	/**
	 * 设备ID
	 * 
	 * @return the gpsTime
	 */
	public Timestamp getGpsTime() {
		return this.gpsTime;
	}

	/**
	 * 设备ID
	 * 
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * GPS时间戳
	 * 
	 * @param gpsTime
	 *            the gpsTime to set
	 */
	public void setGpsTime(Timestamp gpsTime) {
		this.gpsTime = gpsTime;
	}

	/**
	 * 内存状态
	 * 
	 * @return the memory
	 */
	public String getMemory() {
		return this.memory;
	}

	/**
	 * SD卡状态
	 * 
	 * @return the sdCard
	 */
	public String getSdCard() {
		return this.sdCard;
	}

	/**
	 * 打印机状态
	 * 
	 * @return the printer
	 */
	public String getPrinter() {
		return this.printer;
	}

	/**
	 * 是否定时回传
	 * 
	 * @return the isTimerLocate
	 */
	public String getIsTimerLocate() {
		return this.isTimerLocate;
	}

	/**
	 * 是否是定距离回传
	 * 
	 * @return the isDistanceLocate
	 */
	public String getIsDistanceLocate() {
		return this.isDistanceLocate;
	}

	/**
	 * CPU状态
	 * 
	 * @param cpu
	 *            the cpu to set
	 */
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	/**
	 * CPU状态
	 * 
	 * @return
	 */
	public String getCpu() {
		return cpu;
	}

	/**
	 * 内存状态
	 * 
	 * @param memory
	 *            the memory to set
	 */
	public void setMemory(String memory) {
		this.memory = memory;
	}

	/**
	 * SD卡状态
	 * 
	 * @param sdCard
	 *            the sdCard to set
	 */
	public void setSdCard(String sdCard) {
		this.sdCard = sdCard;
	}

	/**
	 * 打印机状态
	 * 
	 * @param printer
	 *            the printer to set
	 */
	public void setPrinter(String printer) {
		this.printer = printer;
	}

	/**
	 * 是否是定时回传
	 * 
	 * @param isTimerLocate
	 *            the isTimerLocate to set
	 */
	public void setIsTimerLocate(String isTimerLocate) {
		this.isTimerLocate = isTimerLocate;
	}

	/**
	 * 是否是定距离回传
	 * 
	 * @param isDistanceLocate
	 *            the isDistanceLocate to set
	 */
	public void setIsDistanceLocate(String isDistanceLocate) {
		this.isDistanceLocate = isDistanceLocate;
	}

	/**
	 * 摄像头
	 * 
	 * @return the camera
	 */
	public String getCamera() {
		return this.camera;
	}

	/**
	 * 摄像头
	 * 
	 * @param camera
	 *            the camera to set
	 */
	public void setCamera(String camera) {
		this.camera = camera;
	}

	/**
	 * 温度值
	 * 
	 * @return the temperator
	 */
	public Float getTemperator() {
		return this.temperator;
	}

	/**
	 * 温度值
	 * 
	 * @param temperator
	 *            the temperator to set
	 */
	public void setTemperator(Float temperator) {
		this.temperator = temperator;
	}

	/**
	 * 熄火状态
	 * 
	 * @return the flameOut
	 */
	public String getFlameOut() {
		return this.flameOut;
	}

	/**
	 * 熄火状态
	 * 
	 * @param flameOut
	 *            the flameOut to set
	 */
	public void setFlameOut(String flameOut) {
		this.flameOut = flameOut;
	}

	/**
	 * 油量
	 * 
	 * @return the oilMass
	 */
	public Float getOilMass() {
		return this.oilMass;
	}

	/**
	 * 停车时间间隔
	 * 
	 * @return the stopCarTimeInterval
	 */
	public String getStopCarTimeInterval() {
		return this.stopCarTimeInterval;
	}

	/**
	 * 温度传感器路数
	 * 
	 * @return the temeratureRouteNum
	 */
	public String getTemeratureRouteNum() {
		return this.temeratureRouteNum;
	}

	/**
	 * 耗油量
	 * 
	 * @return the oilUsed
	 */
	public Float getOilUsed() {
		return this.oilUsed;
	}

	/**
	 * 加油量
	 * 
	 * @return the oilAdded
	 */
	public Float getOilAdded() {
		return this.oilAdded;
	}

	/**
	 * 油量
	 * 
	 * @param oilMass
	 *            the oilMass to set
	 */
	public void setOilMass(Float oilMass) {
		this.oilMass = oilMass;
	}

	/**
	 * 停车时间间隔
	 * 
	 * @param stopCarTimeInterval
	 *            the stopCarTimeInterval to set
	 */
	public void setStopCarTimeInterval(String stopCarTimeInterval) {
		this.stopCarTimeInterval = stopCarTimeInterval;
	}

	/**
	 * 温度传感器路数
	 * 
	 * @param temeratureRouteNum
	 *            the temeratureRouteNum to set
	 */
	public void setTemeratureRouteNum(String temeratureRouteNum) {
		this.temeratureRouteNum = temeratureRouteNum;
	}

	/**
	 * 耗油量
	 * 
	 * @param oilUsed
	 *            the oilUsed to set
	 */
	public void setOilUsed(Float oilUsed) {
		this.oilUsed = oilUsed;
	}

	/**
	 * 加油量
	 * 
	 * @param oilAdded
	 *            the oilAdded to set
	 */
	public void setOilAdded(Float oilAdded) {
		this.oilAdded = oilAdded;
	}

	/**
	 * 状态关联的位置ID
	 * 
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * 状态关联的位置ID
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}