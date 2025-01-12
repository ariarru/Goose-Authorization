'use client'

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { deleteDevice } from './adminServerActions';
import AddNewDeviceForm from './AddNewDeviceForm';
import Card from '../layout/Card';

export default function HandleDevices({ initialDevices = [] }) {
  const router = useRouter();
  const [selectedDevice, setSelectedDevice] = useState(null);

  const handleDeviceAdded = (newDevice) => {
    
    router.refresh();
    setSelectedDevice(null);
  };

  const handleEditDevice = (device) => {
    setSelectedDevice(device);
    console.log(selectedDevice);
  };

  const handleDeleteDevice = async (deviceId) => {
    try {
      const result = await deleteDevice(deviceId);
      if (result.success) {
        alert('Device deleted successfully');
        router.refresh();
      } else {
        alert('Failed to delete device');
      }
    } catch (err) {
      alert('Unexpected error deleting device');
    }
  };


  return (
    <div className="flex flex-col md:flex-row gap-4 w-full">
      {/* Device Form Column */}
      <div className="w-full md:w-1/3">
        <AddNewDeviceForm 
          onDeviceAdded={handleDeviceAdded}
          initialDevice={selectedDevice}
        />
      </div>

      {/* Device List Column */}
      <div className="w-full md:w-2/3 ">
        <h2 className="text-xl  text-gray-400 pt-6 mb-4">
          {initialDevices.length > 0 ? 'Your Devices' : 'No Devices'}
        </h2>
        
        {initialDevices.length > 0 && (
          <div className="flex flex-row gap-4 flex-wrap">
            {initialDevices.map((device) => (
              <Card key={device.device_s_id} className="p-4 shadow-md">
                <div className="flex flex-col">
                  <h3 className="text-lg font-semibold mb-2">{device.device_s_name}</h3>
                  <div className='flex flex-row gap-2 text-sm text-gray-600 mb-1'> 
                    <p className=" font-bold">
                      MAC: 
                    </p>
                    {device.mac}
                  </div>
                  <div className='flex flex-row gap-2 text-sm text-gray-600 mb-1'> 
                    <p className=" font-bold">
                    Category: 
                  </p>
                  {device.category}
                  </div>
                  <div className="flex gap-2 mt-auto">
                    <button
                      onClick={() => handleEditDevice(device)}
                      className="bg-indigo-400 hover:bg-indigo-500 text-white px-2 py-1 rounded text-sm"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeleteDevice(device.device_s_id)}
                      className="bg-red-400 hover:bg-red-500 text-white px-2 py-1 rounded text-sm"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}