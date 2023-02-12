package illyan.plumber.filter

interface Filter<DataType> {
    fun filterData(data: DataType): DataType
}