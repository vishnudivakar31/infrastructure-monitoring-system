import './App.css';
import PCName from './components/PcName'
import DiskUsage from './components/DiskUsage';
import ProcessStats from './components/ProcessStats'
import CpuUsage from './components/CPUUsage';
import NetWorkInterfaces from './components/NetworkInterfaces';
import NetworkSpeed from './components/NetworkSpeed';
import BatteryStatus from './components/BatteryStatus';
import AlarmView from './components/AlarmView';
import { useState } from 'react';
import {BsBatteryCharging, BsCpuFill, BsFillCloudArrowDownFill, BsFillCloudArrowUpFill} from 'react-icons/bs'
import {RiUninstallFill} from 'react-icons/ri'

let webSocket = new WebSocket('ws://localhost:8080/system-stats')

webSocket.onopen = (event) => {
  console.log('websocket opened', event)
}

webSocket.onclose = (event) => {
  console.log('websocket closed', event)
}

webSocket.onerror = (event) => {
  console.log('websocket error happened. ', event)
}

const alarm_type_icon = {
  CPU_ALARM: {
      icon: <BsCpuFill />,
      type: 'CPU Alarm'
  },
  DISK_ALARM: {
      icon: <RiUninstallFill />,
      type: 'Disk Alarm'
  },
  BATTERY_ALARM:  {
      icon: <BsBatteryCharging />,
      type: 'Battery Alarm'
  },
  NETWORK_ALARM_DOWNLOAD: {
      icon:<BsFillCloudArrowDownFill />,
      type: 'Network Download Alarm'
  },
  NETWORK_ALARM_UPLOAD: { 
      icon: <BsFillCloudArrowUpFill />,
      type: 'Network Upload Alarm'
  }
}
const color = {
  WARNING: '#FEA47F',
  ATTENTION: '#F97F51',
  FATAL: '#B33771',
  DOWN: '#6D214F'
}

const alarmSeverity = {
  WARNING: 1,
  ATTENTION: 2,
  FATAL: 3,
  DOWN: 4
}

function App() {
  const [systemStats, setSystemStats] = useState([])
  const [alarms, setAlarms] = useState([])
  webSocket.onmessage = (event) => {
    const data = JSON.parse(event.data)
    if ('alarmType' in data) {
      let updatedAlarm = alarms.filter(alarm => alarm.alarmType !== data.alarmType)
      updatedAlarm = [data, ...updatedAlarm]
      updatedAlarm = updatedAlarm.sort((alarm1, alarm2) => (
        alarmSeverity[alarm1.alarmSeverity] > alarmSeverity[alarm2.alarmSeverity] ? -1 : 
          alarmSeverity[alarm1.alarmSeverity] < alarmSeverity[alarm2.alarmSeverity] ? 1 : 0
      ))
      setAlarms(updatedAlarm)
    } else {
      let updatedStats = systemStats.filter(stat => stat.statName !== data.statName)
      updatedStats.push(data)
      setSystemStats(updatedStats)
    }
  }
  
  return (
    <div className="App">
      <div className="app-header">
        <h3>Infrastructure Monitoring System</h3>
      </div>
      <div className='ctn-display'>
        <div className='stat-ctn'>
          <h3>System Stats ({systemStats.length})</h3>
          <div className='stat-grid'>
            <PCName hostname_stat={systemStats.find(stat => stat.statName === 'HOSTNAME')} />
            <ProcessStats stat={systemStats.find(stat => stat.statName === 'NUMBER_OF_PROCESS')} />
            <CpuUsage stat={systemStats.find(stat => stat.statName === 'CPU_USAGE_STATS')} />
            <NetworkSpeed stat={systemStats.find(stat => stat.statName === 'NETWORK_SPEED_TEST')} />
            <BatteryStatus stat={systemStats.find(stat => stat.statName === 'BATTERY_USAGE_STAT')} />
            <DiskUsage stat={systemStats.find(stat => stat.statName === 'DISK_USAGE')} />
            <NetWorkInterfaces stat={systemStats.find(stat => stat.statName === 'NETWORK_INTERFACE_ADDRESS')} />
          </div>
        </div>
        <div className='alarm-ctn'>
          <div className='header'>
            <div style={{ whiteSpace: 'nowrap' }}>
              <h3>Alarms ({alarms.length})</h3>
            </div>
            <div className='legends'>
              {
                Object.keys(color).map((key, index) => (
                  <div className='legend-title' key={index} style={{ color: color[key] }}>
                    <div id="color-box" style={{ background: color[key] }} />
                    <div>{key}</div>
                  </div>
                ))
              }
            </div>
          </div>
          {
            alarms.map((alarm, index) => <AlarmView key={index} alarm={alarm} alarm_type_icon={alarm_type_icon[alarm.alarmType]} color={color[alarm.alarmSeverity]} />)
          }
        </div>
      </div>
    </div>
  );
}

export default App;
