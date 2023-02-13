package illyan.plumber

import illyan.plumber.filter.Filter
import illyan.plumber.sensor.Sensor

class Pipe<InputDataType, OutputDataType> {
    private val filters = mutableListOf<(InputDataType) -> InputDataType>()
    private var fitting: ((InputDataType) -> OutputDataType)? = null
    private val outputPipes = mutableListOf<(OutputDataType) -> Unit>()

    fun processData(data: InputDataType): InputDataType {
        var processed = data
        filters.forEach {
            processed = it(processed)
        }
        fitting?.let { fitting ->
            val output = fitting(processed)
            outputPipes.forEach { it(output) }
        }
        return processed
    }

    class Builder<InputDataType, OutputDataType> {
        private val pipe = Pipe<InputDataType, OutputDataType>()

        fun addFilter(
            filter: (InputDataType) -> InputDataType
        ): Builder<InputDataType, OutputDataType> {
            pipe.filters.add(filter)
            return this
        }

        fun addFilter(
            filter: Filter<InputDataType>
        ): Builder<InputDataType, OutputDataType> {
            pipe.filters.add(filter::filterData)
            return this
        }

        fun addSensor(
            sensor: (InputDataType) -> Unit
        ): Builder<InputDataType, OutputDataType> {
            pipe.filters.add { sensor(it); it }
            return this
        }

        fun addSensor(
            sensor: Sensor<InputDataType>
        ): Builder<InputDataType, OutputDataType> {
            pipe.filters.add { sensor.monitorData(it); it }
            return this
        }

        fun setFitting(
            fitting: (InputDataType) -> OutputDataType
        ): Builder<InputDataType, OutputDataType> {
            pipe.fitting = fitting
            return this
        }

        fun addOutputPipe(
            outputPipe: (OutputDataType) -> Unit
        ): Builder<InputDataType, OutputDataType> {
            pipe.outputPipes.add(outputPipe)
            return this
        }

        fun addOutputPipe(
            outputPipe: Pipe<OutputDataType, Any>
        ): Builder<InputDataType, OutputDataType> {
            pipe.outputPipes.add(outputPipe::processData)
            return this
        }

        fun build() = pipe
    }
}