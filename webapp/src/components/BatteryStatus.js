import {RiTimeLine} from 'react-icons/ri'
import {BsBatteryCharging} from 'react-icons/bs'
import moment from 'moment'

export default function BatteryStatus({stat}) {
    let prev_stat = {
        measure: [],
        timestamp: Date.now()
    }
    prev_stat = stat ? stat : prev_stat
    if (prev_stat.measure.length === 2)
        prev_stat.measure[1] = prev_stat.measure[1].replace(/\s\s+/g, ' ')
    return (
        <div className='infra-name-ctn' id='battery'>
            <h4>Battery Health</h4>
            {prev_stat.measure.map((stat, index) => (
                <div className="measure_stat" key={index}>
                    <BsBatteryCharging />
                    <p>{stat}</p>
                </div>)
            )}
            <div className='measure_stat' id='timestamp'>
                <RiTimeLine />
                <p>updated {moment(prev_stat.timestamp).fromNow()}</p>
            </div>
        </div>
    )
}