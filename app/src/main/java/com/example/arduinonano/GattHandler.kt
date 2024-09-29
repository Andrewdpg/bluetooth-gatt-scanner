package com.example.arduinonano

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*

class GattHandler(private val context: Context) {
    private var bluetoothGatt: BluetoothGatt? = null
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") // UUID for Client Characteristic Configuration Descriptor (CCCD)

    @SuppressLint("MissingPermission")
    fun connectGatt(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun disconnectGatt() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        Log.d("GattHandler", "Disconnected from GATT server.")
    }

    private val gattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.d("GattHandler", "Connected to GATT server.")
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.d("GattHandler", "Disconnected from GATT server.")
                bluetoothGatt?.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("GattHandler", "Services discovered.")

                gatt?.services?.forEach { service ->
                    Log.d("GattHandler", "Service UUID: ${service.uuid}")

                    service.characteristics.forEach { characteristic ->
                        Log.d("GattHandler", "Characteristic UUID: ${characteristic.uuid}")

                        enableNotification(gatt, characteristic)

                        readCharacteristic(gatt, characteristic)
                    }
                }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristic?.let {
                    val value = it.value?.joinToString(", ") ?: "No value"
                    Log.d("GattHandler", "Characteristic ${it.uuid} read value: $value")
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            characteristic?.let {
                val updatedValue = it.value?.joinToString(", ") ?: "No value"
                Log.d("GattHandler", "Characteristic ${it.uuid} changed value: $updatedValue")
            }
        }

        @SuppressLint("MissingPermission")
        private fun enableNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                gatt.setCharacteristicNotification(characteristic, true)

                val descriptor = characteristic.getDescriptor(CCCD_UUID)
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)

                Log.d("GattHandler", "Notifications enabled for ${characteristic.uuid}")
            } else {
                Log.d("GattHandler", "Characteristic ${characteristic.uuid} does not support notifications")
            }
        }

        @SuppressLint("MissingPermission")
        private fun readCharacteristic(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            gatt.readCharacteristic(characteristic)
        }
    }
}
