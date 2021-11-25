import {RiTimeLine} from 'react-icons/ri'
import {BsCpuFill} from 'react-icons/bs'
import moment from 'moment'

export default function CpuUsage({stat}) {
    let prev_stat = {
        measure: 'fetching details',
        timestamp: Date.now()
    }
    prev_stat = stat ? stat : prev_stat
    return (
        <div className='infra-name-ctn' id='cpu'>
            <h4>CPU Usages</h4>
            <div className='measure_stat'>
                <BsCpuFill />
                <p>{prev_stat.measure}</p>
            </div>
            <div className='measure_stat' id='timestamp'>
                <RiTimeLine />
                <p>updated {moment(prev_stat.timestamp).fromNow()}</p>
            </div>
        </div>
    )
}