'use client'

import { useState } from 'react';
import { insertDevice, updateDevice } from './adminServerActions';

export default function AddNewDeviceForm({ 
  onDeviceAdded, 
  initialDevice 
}) {
    if(!initialDevice){
        initialDevice = null;
    }
  const [formData, setFormData] = useState({
    name: initialDevice?.device_s_name || '',
    macAddress: initialDevice?.mac || '',
    category: initialDevice?.category || ''
  });

  const [errors, setErrors] = useState({});

    // Funzione per validare i campi del modulo prima dell'invio
  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) {
      newErrors.name = 'Device name is required';
    }

    const macAddressRegex = /^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$/;
    if (!formData.macAddress.trim()) {
      newErrors.macAddress = 'MAC address is required';
    } else if (!macAddressRegex.test(formData.macAddress)) {
      newErrors.macAddress = 'Invalid MAC address format';
    }

    if (!formData.category) {
      newErrors.category = 'Category is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

    // Gestore per gli input del modulo
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: undefined
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      let result;
      if (initialDevice) {

        result = await updateDevice(initialDevice.device_s_id, formData);
      } else {
                // Inserisce nuovo dispositivo
        result = await insertDevice(formData);
      }

      if (result.success) {
        alert('Device updated successfully');
        onDeviceAdded && onDeviceAdded(result.device);

        // Reset form
        setFormData({
          name: '',
          macAddress: '',
          category: ''
        });
        alert('Device operation successful');
      } else {
        console.error('Device operation failed:', result.error);
      }
    } catch (error) {
      console.error('Unexpected error:', error);
    }
  };

  return (
    <form 
      onSubmit={handleSubmit} 
      className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4"
    >
      <h2 className="text-xl text-gray-400 mb-4">
        {initialDevice ? 'Edit Device' : 'Add New Device'}
      </h2>

      <div className="mb-4">
        <label 
          className="block text-gray-500 text-sm mb-2" 
          htmlFor="name"
        >
          Device Name
        </label>
        <input
          type="text"
          name="name"
          id="name"
          value={formData.name}
          onChange={handleInputChange}
          className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-500 leading-tight focus:outline-none focus:shadow-outline ${
            errors.name ? 'border-red-500' : ''
          }`}
          placeholder="Enter device name"
        />
        {errors.name && (
          <p className="text-red-500 text-xs italic">{errors.name}</p>
        )}
      </div>

      <div className="mb-4">
        <label 
          className="block text-gray-500 text-sm mb-2" 
          htmlFor="macAddress"
        >
          MAC Address
        </label>
        <input
          type="text"
          name="macAddress"
          id="macAddress"
          value={formData.macAddress}
          onChange={handleInputChange}
          className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-500 leading-tight focus:outline-none focus:shadow-outline ${
            errors.macAddress ? 'border-red-500' : ''
          }`}
          placeholder="XX:XX:XX:XX:XX:XX"
        />
        {errors.macAddress && (
          <p className="text-red-500 text-xs italic">{errors.macAddress}</p>
        )}
      </div>

      <div className="mb-4">
        <label 
          className="block text-gray-500 text-sm mb-2" 
          htmlFor="category"
        >
          Category
        </label>
        <select
          name="category"
          id="category"
          value={formData.category}
          onChange={handleInputChange}
          className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-500 leading-tight focus:outline-none focus:shadow-outline ${
            errors.category ? 'border-red-500' : ''
          }`}
        >
          <option value="">Select Category</option>
          <option value="Smartwatch">Smartwatch</option>
          <option value="Mobile Phone">Mobile Phone</option>
          <option value="Wearable Tech">Wearable Tech</option>
        </select>
        {errors.category && (
          <p className="text-red-500 text-xs italic">{errors.category}</p>
        )}
      </div>

      <div className="flex items-center justify-between">
        <button
          type="submit"
          className="bg-indigo-400 hover:bg-indigo-700 text-white py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        >
          {initialDevice ? 'Update Device' : 'Add Device'}
        </button>
      </div>
    </form>
  );
}