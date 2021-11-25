import {RiComputerLine, RiTimeLine} from 'react-icons/ri'
import moment from 'moment'

export default function PCName({hostname_stat}) {
    let prev_stat = {
        measure: 'fetching details',
        timestamp: Date.now()
    }
    prev_stat = hostname_stat ? hostname_stat : prev_stat
    return (
        <div className='infra-name-ctn' id='pc-name'>
            <h4>Monitoring System</h4>
            <div className='measure_stat'>
                <RiComputerLine />
                <p>{prev_stat.measure}</p>
            </div>
            <div className='measure_stat' id='timestamp'>
                <RiTimeLine />
                <p>updated {moment(prev_stat.timestamp).fromNow()}</p>
            </div>
        </div>
    )
}