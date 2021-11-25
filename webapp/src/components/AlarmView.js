
import {RiTimeLine} from 'react-icons/ri'
import moment from 'moment'

export default function AlarmView({alarm, alarm_type_icon, color}) {
    return (
        <div className='alarm-view' style={{ color: color}}>
            <div id='header'>
                <div>
                    {alarm_type_icon.icon}
                <div>{alarm_type_icon.type}</div>
                </div>
                <div>
                    {alarm.alarmSeverity}
                </div>
            </div>
            <div id="message">
                {alarm.message}
            </div>
            <div id="timestamp">
                <RiTimeLine />
                <p>updated {moment(alarm.timestamp).fromNow()}</p>
            </div>
        </div>
    )
}