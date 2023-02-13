package illyan.plumber.sensor

interface Sensor<DataType> {
    fun monitorData(data: DataType)
}